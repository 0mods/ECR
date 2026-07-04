package com.algorithmlx.ecr.api.multiblock

import net.minecraft.client.renderer.block.BlockAndTintGetter
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.RegistryAccess
import net.minecraft.core.registries.BuiltInRegistries
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

class Multiblock(block: Multiblock.() -> Unit): BlockAndTintGetter {
    private val blockEntities = hashMapOf<BlockPos, BlockEntity>()
    lateinit var registryAccess: RegistryAccess
    var xSize: Int = 0
    var ySize: Int = 0
    var zSize: Int = 0
    val blocks = arrayListOf<MultiblockMatcher>()

    init {
        block()
    }

    fun size(xSize: Int, zSize: Int, ySize: Int) {
        this.xSize = xSize
        this.zSize = zSize
        this.ySize = ySize
    }

    fun pattern(vararg blocks: MultiblockMatcher?) {
        assert(blocks.size != xSize * ySize * zSize) { "Blocks must have the same size" }

        this.blocks.clear()
        this.blocks.addAll(blocks.map { it ?: empty() })
    }

    fun isValid(level: Level, basePos: BlockPos) = listOf(Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST).any {
        checkStructureForDirection(level, basePos, it)
    }

    private fun checkStructureForDirection(level: Level, basePos: BlockPos, direction: Direction): Boolean {
        for (offsetX in 0 ..< xSize) {
            for (offsetY in 0 ..< ySize) {
                for (offsetZ in 0 ..< zSize) {
                    if (checkFromBase(level, basePos, direction, offsetX, offsetY, offsetZ)) return true
                }
            }
        }

        return false
    }

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
                    val expectedBlock = blocks[x + z * zSize + y * zSize * xSize]
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
        val id = pos.x + pos.z * zSize + pos.y * zSize * xSize
        if (id !in blocks.indices) return Blocks.LIGHT.defaultBlockState()
        return blocks[id].default()
    }

    override fun getFluidState(pos: BlockPos): FluidState = this.getBlockState(pos).fluidState

    override fun getHeight(): Int = ySize

    override fun getMinY(): Int = 0

    fun empty() = block(Blocks.AIR.defaultBlockState())

    fun block(state: BlockState, ignoreTag: Boolean = false) = object : MultiblockMatcher {
        override fun matches(block: BlockState): Boolean {
            return if (ignoreTag) block.`is`(state.block) else block == state
        }

        override fun default() = state
    }

    fun tag(tag: TagKey<Block>) = object : MultiblockMatcher {
        override fun matches(block: BlockState) = block.`is`(tag)

        override fun default(): BlockState = BuiltInRegistries.BLOCK
            .getTagOrEmpty(tag)
            .firstOrNull()?.value()
            ?.defaultBlockState() ?: Blocks.AIR.defaultBlockState()
    }
}

interface MultiblockMatcher {
    fun matches(block: BlockState): Boolean

    fun default(): BlockState
}
