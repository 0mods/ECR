package team._0mods.ecr.common.blocks

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.EnumProperty
import net.minecraft.world.phys.shapes.BooleanOp
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import team._0mods.ecr.api.block.Multipart
import team._0mods.ecr.api.block.client.LowSizeBreakParticle
import team._0mods.ecr.common.blocks.part.CrystalPart

class CrystalBlock(properties: Properties) : Block(properties), Multipart<CrystalPart>, LowSizeBreakParticle {
    companion object {
        @JvmField
        val PART: EnumProperty<CrystalPart> = EnumProperty.create("part", CrystalPart::class.java)
    }

    init {
        this.registerDefaultState(
            this.stateDefinition.any()
                .setValue(PART, CrystalPart.DOWN)
        )
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(PART)
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState? {
        val level = context.level
        val dir = context.horizontalDirection
        val pos = context.clickedPos

        if (canPlaceAllParts(level, pos, dir, context))
            return this.defaultBlockState().setValue(PART, CrystalPart.DOWN)

        return null
    }

    override fun setPlacedBy(level: Level, pos: BlockPos, state: BlockState, placer: LivingEntity?, stack: ItemStack) {
        val positions = this.getAllParts(pos, Direction.NORTH)

        positions.forEachIndexed { i, p ->
            level.setBlock(p, state.setValue(PART, if (i == 0) CrystalPart.DOWN else CrystalPart.UP), 3)
        }
    }

    override fun playerWillDestroy(level: Level, pos: BlockPos, state: BlockState, player: Player) {
        val part = state.getValue(PART)
        val otherPos = if (part == CrystalPart.DOWN) pos.above() else pos.below()
        val otherState = level.getBlockState(otherPos)

        if (otherState.`is`(this) && otherState.getValue(PART) != part) {
            level.setBlock(otherPos, Blocks.AIR.defaultBlockState(), 35)
            level.levelEvent(player, 2001, otherPos, getId(otherState))
        }

        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 35)
        level.levelEvent(player, 2001, pos, getId(state))

        super.playerWillDestroy(level, pos, state, player)
    }

    @Suppress("OVERRIDE_DEPRECATION", "WHEN_ENUM_CAN_BE_NULL_IN_JAVA")
    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
        val shape = state.getValue(PART)
        return when(shape) {
            CrystalPart.DOWN -> makeDownShape()
            CrystalPart.UP -> makeUpShape()
        }
    }

    override fun getBasePos(pos: BlockPos, dir: Direction, part: CrystalPart): BlockPos {
        val positions = this.getAllParts(pos, dir)
        return when(part) {
            CrystalPart.DOWN -> positions[0]
            CrystalPart.UP -> positions[1]
        }
    }

    override fun getAllParts(pos: BlockPos, dir: Direction): Array<BlockPos> = arrayOf(pos, pos.above())

    private fun makeDownShape(): VoxelShape {
        var shape = Shapes.empty()
        shape = Shapes.join(shape, Shapes.box(0.4375, 0.0625, 0.4375, 0.5625, 1.0625, 0.5625), BooleanOp.OR)
        shape = Shapes.join(shape, Shapes.box(0.375, 0.1875, 0.375, 0.625, 1.0625, 0.625), BooleanOp.OR)
        shape = Shapes.join(shape, Shapes.box(0.3125, 0.4375, 0.3125, 0.6875, 1.0625, 0.6875), BooleanOp.OR)
        shape = Shapes.join(shape, Shapes.box(0.25, 0.75, 0.25, 0.75, 1.0625, 0.75), BooleanOp.OR)

        return shape
    }

    private fun makeUpShape(): VoxelShape {
        var shape = Shapes.empty()
        shape = Shapes.join(shape, Shapes.box(0.4375, .0, 0.4375, 0.5625, 0.9375, 0.5625), BooleanOp.OR)
        shape = Shapes.join(shape, Shapes.box(0.375, .0, 0.375, 0.625, 0.8125, 0.625), BooleanOp.OR)
        shape = Shapes.join(shape, Shapes.box(0.3125, .0, 0.3125, 0.6875, 0.625, 0.6875), BooleanOp.OR)
        shape = Shapes.join(shape, Shapes.box(0.25, .0, 0.25, 0.75, 0.3125, 0.75), BooleanOp.OR)

        return shape
    }
}