package com.algorithmlx.ecr.api.multiblock

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.client.renderer.block.BlockAndTintGetter
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.RegistryAccess
import net.minecraft.core.registries.Registries
import net.minecraft.tags.TagKey
import net.minecraft.world.level.CardinalLighting
import net.minecraft.world.level.ColorResolver
import net.minecraft.world.level.Level
import net.minecraft.world.level.biome.Biomes
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.lighting.LevelLightEngine
import net.minecraft.world.level.material.FluidState

open class Multiblock(val xSize: Int, val zSize: Int, val ySize: Int, block: Multiblock.() -> Unit): BlockAndTintGetter {
    constructor(xSize: Int, zSize: Int, ySize: Int, blocks: List<MultiblockMatcher>): this(xSize, ySize, zSize, {}) {
        require(blocks.size == xSize * ySize * zSize) { "Blocks must have the same size: expected ${xSize * ySize * zSize} got ${blocks.size}" }
        this.blocks.clear()
        this.blocks.addAll(blocks)
    }

    companion object {
        @JvmField
        val CODEC: Codec<Multiblock> = RecordCodecBuilder.create {
            it.group(
                Codec.INT.fieldOf("x_size").forGetter(Multiblock::xSize),
                Codec.INT.fieldOf("z_size").forGetter(Multiblock::zSize),
                Codec.INT.fieldOf("y_size").forGetter(Multiblock::ySize),
                MultiblockMatcher.CODEC.listOf().fieldOf("blocks")
                    .forGetter { mb -> mb.blocks }
            ).apply(it, ::Multiblock)
        }
    }

    private val blockEntities = hashMapOf<BlockPos, BlockEntity>()
    lateinit var registryAccess: RegistryAccess
    val blocks = arrayListOf<MultiblockMatcher>()

    private val volume = xSize * ySize * zSize

    init {
        block()
    }

    fun pattern(vararg blocks: MultiblockMatcher?) {
        require(blocks.size == volume) { "Blocks must have the same size: expected $volume got ${blocks.size}" }

        this.blocks.clear()
        this.blocks.addAll(blocks.map { it ?: empty() })
    }

    fun findPlacement(level: Level, basePos: BlockPos): MultiblockPlacement? {
        listOf(Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST).forEach { direction ->
            (0 ..< xSize).forEach { startX ->
                (0 ..< ySize).forEach { startY ->
                    (0 ..< zSize).forEach { startZ ->
                        if (checkFromBase(level, basePos, direction, startX, startY, startZ)) return MultiblockPlacement(
                            basePos, direction, startX, startY, startZ
                        )
                    }
                }
            }
        }

        return null
    }

    fun replaceInWorld(
        level: Level,
        placement: MultiblockPlacement,
        replaceAir: Boolean = false,
        transform: (BlockState) -> BlockState
    ) {
        (0 ..< ySize).forEach { y ->
            (0 ..< zSize).forEach { z ->
                (0 ..< xSize).forEach fe@{ x ->
                    val oldDefaultState = blocks[indexOf(x, y, z)].default()

                    if (!replaceAir && oldDefaultState.isAir) return@fe

                    val newState = transform(oldDefaultState)
                    val pos = getRotatedPos(
                        placement.basePos,
                        x - placement.startX, y - placement.startY, z - placement.startZ,
                        placement.direction
                    )

                    level.setBlock(pos, newState, Block.UPDATE_ALL)
                }
            }
        }
    }

    fun replaceCurrentBlocks(
        level: Level,
        placement: MultiblockPlacement,
        transform: (BlockState) -> BlockState,
        replaceAir: Boolean
    ) {
        (0 ..< ySize).forEach { y ->
            (0 ..< zSize).forEach { z ->
                (0 ..< xSize).forEach fe@{ x ->
                    val matcher = blocks[indexOf(x, y, z)]

                    if (!replaceAir && matcher.default().isAir) return@fe

                    val pos = getRotatedPos(
                        placement.basePos,
                        x - placement.startX, y - placement.startY, z - placement.startZ,
                        placement.direction
                    )

                    val currentState = level.getBlockState(pos)

                    if (!matcher.matches(currentState)) return@fe

                    level.setBlock(pos, transform(currentState), Block.UPDATE_ALL)
                }
            }
        }
    }

    private fun indexOf(x: Int, y: Int, z: Int): Int = x + z * xSize + y * xSize *zSize

    private fun checkFromBase(
        level: Level,
        basePos: BlockPos,
        direction: Direction,
        startX: Int,
        startY: Int,
        startZ: Int,
    ): Boolean {
        for (y in 0..<ySize) {
            for (z in 0..<zSize) {
                for (x in 0..<xSize) {
                    val expectedBlock = blocks[indexOf(x, y, z)]
                    if (expectedBlock.default().isAir) continue

                    val rotatedPos = getRotatedPos(basePos, x - startX, y - startY, z - startZ, direction)
                    val currentBlock = level.getBlockState(rotatedPos)

                    if (!expectedBlock.matches(currentBlock)) return false
                }
            }
        }
        return true
    }

    private fun getRotatedPos(basePos: BlockPos, x: Int, y: Int, z: Int, direction: Direction): BlockPos {
        return when (direction) {
            Direction.NORTH -> basePos.offset(x, y, -z)
            Direction.SOUTH -> basePos.offset(-x, y, z)
            Direction.WEST -> basePos.offset(-z, y, -x)
            Direction.EAST -> basePos.offset(z, y, x)
            else -> basePos
        }
    }

    override fun cardinalLighting(): CardinalLighting = CardinalLighting.DEFAULT

    override fun getBlockTint(
        pos: BlockPos,
        color: ColorResolver
    ): Int = color.getColor(
        registryAccess.getOrThrow(Registries.BIOME).value().getOrThrow(Biomes.PLAINS).value(),
        pos.x.toDouble(),
        pos.z.toDouble()
    )

    override fun getLightEngine(): LevelLightEngine = LevelLightEngine.EMPTY

    override fun getBlockEntity(pos: BlockPos): BlockEntity? {
        val state = this.getBlockState(pos)
        if (state.block is EntityBlock)
            return blockEntities.computeIfAbsent(pos.immutable()) { p ->
                (state.block as EntityBlock).newBlockEntity(p, state) ?: throw IllegalStateException("Block does not have a BlockEntity")
            }
        return null
    }

    override fun getBlockState(pos: BlockPos): BlockState {
        if (pos.x !in 0 ..< xSize || pos.y !in 0 ..< ySize || pos.z !in 0 ..< zSize) return Blocks.LIGHT.defaultBlockState()

        val id = indexOf(pos.x, pos.y, pos.z)
        if (id !in blocks.indices) return Blocks.LIGHT.defaultBlockState()

        return blocks[id].default()
    }

    override fun getFluidState(pos: BlockPos): FluidState = this.getBlockState(pos).fluidState

    override fun getHeight(): Int = ySize

    override fun getMinY(): Int = 0

    fun empty() = block(Blocks.AIR.defaultBlockState())
    fun block(state: BlockState, ignoreTag: Boolean = false) = BlockMultiblockMatcher(state, ignoreTag)
    fun tag(tag: TagKey<Block>) = TagMultiblockMatcher(tag)
}

data class MultiblockPlacement(
    val basePos: BlockPos,
    val direction: Direction,
    val startX: Int,
    val startY: Int,
    val startZ: Int
)
