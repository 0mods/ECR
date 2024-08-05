package team._0mods.ecr.common.blocks

import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.BooleanOp
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import net.minecraftforge.network.NetworkHooks
import team._0mods.ecr.common.blocks.entity.MithrilineFurnaceEntity
import team._0mods.ecr.common.init.registry.ECMultiblocks


class MithrilineFurnace(properties: Properties) : BaseEntityBlock(
    properties.noOcclusion()
) {
    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity = MithrilineFurnaceEntity(pos, state)

    override fun <T : BlockEntity> getTicker(
        level: Level,
        state: BlockState,
        blockEntityType: BlockEntityType<T>
    ): BlockEntityTicker<T> {
        return BlockEntityTicker<T> { l, bp, s, e -> MithrilineFurnaceEntity.onTick(l, bp, s, e as MithrilineFurnaceEntity) }
    }

    @Suppress("OVERRIDE_DEPRECATION")
    override fun use(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hand: InteractionHand,
        hit: BlockHitResult
    ): InteractionResult {
        if (ECMultiblocks.mithrilineFurnace.isComplete(level, pos)) {
            if (!level.isClientSide) {
                val blockEntity = level.getBlockEntity(pos)
                if (blockEntity is MithrilineFurnaceEntity) {
                    NetworkHooks.openScreen(player as ServerPlayer, blockEntity, blockEntity.blockPos)
                } else throw IllegalStateException("Can not open any block entity that is not instanceof MithrilineFurnaceEntity")
            }

            return InteractionResult.SUCCESS
        } else return InteractionResult.FAIL
    }

    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape = makeShape()

    private fun makeShape(): VoxelShape {
        var shape = Shapes.empty()
        shape = Shapes.join(shape, Shapes.box(0.875, 0.0, 0.0, 1.0, 0.125, 1.0), BooleanOp.OR)
        shape = Shapes.join(shape, Shapes.box(0.0, 0.375, 0.125, 0.125, 0.625, 0.875), BooleanOp.OR)
        shape = Shapes.join(shape, Shapes.box(0.0, 0.875, 0.0, 0.125, 1.0, 1.0), BooleanOp.OR)
        shape = Shapes.join(shape, Shapes.box(0.0, 0.0, 0.0, 0.125, 0.125, 1.0), BooleanOp.OR)
        shape = Shapes.join(shape, Shapes.box(0.125, 0.875, 0.875, 0.875, 1.0, 1.0), BooleanOp.OR)
        shape = Shapes.join(shape, Shapes.box(0.0, 0.125, 0.875, 0.125, 0.875, 1.0), BooleanOp.OR)
        shape = Shapes.join(shape, Shapes.box(0.125, 0.0, 0.875, 0.875, 0.125, 1.0), BooleanOp.OR)
        shape = Shapes.join(shape, Shapes.box(0.125, 0.375, 0.875, 0.875, 0.625, 1.0), BooleanOp.OR)
        shape = Shapes.join(shape, Shapes.box(0.875, 0.875, 0.0, 1.0, 1.0, 1.0), BooleanOp.OR)
        shape = Shapes.join(shape, Shapes.box(0.875, 0.375, 0.125, 1.0, 0.625, 0.875), BooleanOp.OR)
        shape = Shapes.join(shape, Shapes.box(0.875, 0.125, 0.875, 1.0, 0.875, 1.0), BooleanOp.OR)
        shape = Shapes.join(shape, Shapes.box(0.0, 0.125, 0.0, 0.125, 0.875, 0.125), BooleanOp.OR)
        shape = Shapes.join(shape, Shapes.box(0.125, 0.375, 0.0, 0.875, 0.625, 0.125), BooleanOp.OR)
        shape = Shapes.join(shape, Shapes.box(0.125, 0.875, 0.0, 0.875, 1.0, 0.125), BooleanOp.OR)
        shape = Shapes.join(shape, Shapes.box(0.875, 0.125, 0.0, 1.0, 0.875, 0.125), BooleanOp.OR)
        shape = Shapes.join(shape, Shapes.box(0.125, 0.0, 0.0, 0.875, 0.125, 0.125), BooleanOp.OR)
        shape = Shapes.join(shape, Shapes.box(0.125, 0.875, 0.125, 0.875, 0.9375, 0.875), BooleanOp.OR)
        shape = Shapes.join(shape, Shapes.box(0.125, 0.0625, 0.125, 0.1875, 0.125, 0.875), BooleanOp.OR)
        shape = Shapes.join(shape, Shapes.box(0.125, 0.0625, 0.125, 0.875, 0.125, 0.875), BooleanOp.OR)

        return shape
    }
}