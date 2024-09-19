package team._0mods.ecr.common.blocks

import net.minecraft.core.BlockPos
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.BooleanOp
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import team._0mods.ecr.common.api.PropertiedEntityBlock
import team._0mods.ecr.api.block.checkAndOpenMenu
import team._0mods.ecr.api.block.client.LowSizeBreakParticle
import team._0mods.ecr.api.block.prepareDrops
import team._0mods.ecr.api.block.simpleTicker
import team._0mods.ecr.common.blocks.entity.MithrilineFurnaceEntity
import team._0mods.ecr.common.init.registry.ECMultiblocks

class MithrilineFurnace(properties: Properties) : PropertiedEntityBlock(properties), LowSizeBreakParticle {
    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity = MithrilineFurnaceEntity(pos, state)

    override fun <T : BlockEntity> getTicker(
        level: Level,
        state: BlockState,
        blockEntityType: BlockEntityType<T>
    ): BlockEntityTicker<T> = simpleTicker<T, MithrilineFurnaceEntity>(MithrilineFurnaceEntity::onTick)

    @Suppress("OVERRIDE_DEPRECATION")
    override fun use(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hand: InteractionHand,
        hit: BlockHitResult
    ): InteractionResult {
        return if (ECMultiblocks.mithrilineFurnace.isComplete(level, pos)) {
            checkAndOpenMenu<MithrilineFurnaceEntity>(player, level, pos)
        } else InteractionResult.FAIL
    }

    @Suppress("OVERRIDE_DEPRECATION", "DEPRECATION")
    override fun onRemove(state: BlockState, level: Level, pos: BlockPos, newState: BlockState, isMoving: Boolean) {
        prepareDrops<MithrilineFurnaceEntity>(state, level, pos, newState)

        super.onRemove(state, level, pos, newState, isMoving)
    }

    @Suppress("OVERRIDE_DEPRECATION")
    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape = makeShape()

    private fun makeShape(): VoxelShape {
        var shape = Shapes.empty()
        shape = Shapes.join(shape, Shapes.box(0.125, 0.875, 0.125, 0.875, 0.9375, 0.875), BooleanOp.OR)
        shape = Shapes.join(shape, Shapes.box(0.0, 0.0, 0.0, 1.0, 0.125, 0.125), BooleanOp.OR)
        shape = Shapes.join(shape, Shapes.box(0.0, 0.0, 0.875, 1.0, 0.125, 1.0), BooleanOp.OR)
        shape = Shapes.join(shape, Shapes.box(0.0, 0.0, 0.125, 0.125, 0.125, 0.875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.875, 0.0, 0.125, 1.0, 0.125, 0.875), BooleanOp.OR)
        shape = Shapes.join(shape, Shapes.box(0.0, 0.875, 0.0, 1.0, 1.0, 0.125), BooleanOp.OR)
        shape = Shapes.join(shape, Shapes.box(0.0, 0.875, 0.875, 1.0, 1.0, 1.0), BooleanOp.OR)
        shape = Shapes.join(shape, Shapes.box(0.0, 0.875, 0.125, 0.125, 1.0, 0.875), BooleanOp.OR)
        shape = Shapes.join(shape, Shapes.box(0.875, 0.875, 0.125, 1.0, 1.0, 0.875), BooleanOp.OR)
        shape = Shapes.join(shape, Shapes.box(0.0, 0.125, 0.875, 0.125, 0.875, 1.0), BooleanOp.OR)
        shape = Shapes.join(shape, Shapes.box(0.0, 0.125, 0.0, 0.125, 0.875, 0.125), BooleanOp.OR)
        shape = Shapes.join(shape, Shapes.box(0.875, 0.125, 0.0, 1.0, 0.875, 0.125), BooleanOp.OR)
        shape = Shapes.join(shape, Shapes.box(0.875, 0.125, 0.875, 1.0, 0.875, 1.0), BooleanOp.OR)
        shape = Shapes.join(shape, Shapes.box(0.125, 0.0625, 0.125, 0.875, 0.125, 0.875), BooleanOp.OR)
        shape = Shapes.join(shape, Shapes.box(0.0, 0.375, 0.0, 1.0, 0.625, 1.0), BooleanOp.OR)

        return shape
    }

    override fun getRenderShape(state: BlockState): RenderShape = RenderShape.MODEL
}