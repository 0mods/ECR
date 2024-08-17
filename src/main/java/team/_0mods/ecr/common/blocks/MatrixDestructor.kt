package team._0mods.ecr.common.blocks

import net.minecraft.core.BlockPos
import net.minecraft.core.NonNullList
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.Containers
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.BaseEntityBlock
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
import net.minecraftforge.common.capabilities.ForgeCapabilities
import net.minecraftforge.network.NetworkHooks
import team._0mods.ecr.api.block.MRUGenerator
import team._0mods.ecr.api.block.client.LowSizeBreakParticle
import team._0mods.ecr.common.blocks.entity.MatrixDestructorEntity

@Suppress("OVERRIDE_DEPRECATION")
class MatrixDestructor(properties: Properties) : BaseEntityBlock(properties), MRUGenerator, LowSizeBreakParticle {
    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity = MatrixDestructorEntity(pos, state)

    override fun <T : BlockEntity?> getTicker(
        level: Level,
        state: BlockState,
        blockEntityType: BlockEntityType<T>
    ): BlockEntityTicker<T> = BlockEntityTicker { l, bp, s, e ->
        MatrixDestructorEntity.onTick(l, bp, s, e as MatrixDestructorEntity)
    }

    override fun use(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hand: InteractionHand,
        hit: BlockHitResult
    ): InteractionResult {
        if (!level.isClientSide) {
            val be = level.getBlockEntity(pos)
            if (be is MatrixDestructorEntity) {
                NetworkHooks.openScreen(player as ServerPlayer, be, pos)
            } else throw IllegalStateException("Can not open any block entity that is not instanceof MatrixDestructorEntity")
        }

        return InteractionResult.SUCCESS
    }

    override fun onRemove(state: BlockState, level: Level, pos: BlockPos, newState: BlockState, isMoving: Boolean) {
        if (state.block != newState.block) {
            val be = level.getBlockEntity(pos)
            if (be is MatrixDestructorEntity) {
                be.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent {
                    val nnl = NonNullList.withSize(it.slots, ItemStack.EMPTY)
                    for (i in 0 ..< it.slots) {
                        nnl[i] = it.getStackInSlot(i)
                    }

                    Containers.dropContents(level, pos, nnl)
                }
            }
        }

        super.onRemove(state, level, pos, newState, isMoving)
    }

    override fun getRenderShape(state: BlockState): RenderShape = RenderShape.MODEL

    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape =
        makeShape()

    private fun makeShape(): VoxelShape {
        var shape = Shapes.empty()
        shape = Shapes.join(shape, Shapes.box(0.0, 0.0, 0.0, 0.125, 0.9375, 0.125), BooleanOp.OR)
        shape = Shapes.join(shape, Shapes.box(0.0, 0.0, 0.875, 0.125, 0.9375, 1.0), BooleanOp.OR)
        shape = Shapes.join(shape, Shapes.box(0.875, 0.0, 0.875, 1.0, 0.9375, 1.0), BooleanOp.OR)
        shape = Shapes.join(shape, Shapes.box(0.875, 0.0, 0.0, 1.0, 0.9375, 0.125), BooleanOp.OR)
        shape = Shapes.join(shape, Shapes.box(0.0625, 0.0625, 0.0625, 0.9375, 0.5625, 0.9375), BooleanOp.OR)
        shape = Shapes.join(shape, Shapes.box(0.25, 0.5625, 0.25, 0.75, 0.625, 0.75), BooleanOp.OR)
        shape = Shapes.join(shape, Shapes.box(0.0, 0.5, 0.0, 1.0, 0.5625, 1.0), BooleanOp.OR)
        shape = Shapes.join(shape, Shapes.box(0.0, 0.875, 0.0, 0.1875, 0.9375, 0.1875), BooleanOp.OR)
        shape = Shapes.join(shape, Shapes.box(0.8125, 0.875, 0.0, 1.0, 0.9375, 0.1875), BooleanOp.OR)
        shape = Shapes.join(shape, Shapes.box(0.0, 0.875, 0.8125, 0.1875, 0.9375, 1.0), BooleanOp.OR)
        shape = Shapes.join(shape, Shapes.box(0.8125, 0.875, 0.8125, 1.0, 0.9375, 1.0), BooleanOp.OR)
        shape = Shapes.join(shape, Shapes.box(0.0, 0.5625, 0.625, 0.125, 0.8125, 0.75), BooleanOp.OR)
        shape = Shapes.join(shape, Shapes.box(0.0, 0.5625, 0.25, 0.125, 0.8125, 0.375), BooleanOp.OR)
        shape = Shapes.join(shape, Shapes.box(0.25, 0.5625, 0.0, 0.375, 0.8125, 0.125), BooleanOp.OR)
        shape = Shapes.join(shape, Shapes.box(0.625, 0.5625, 0.0, 0.75, 0.8125, 0.125), BooleanOp.OR)
        shape = Shapes.join(shape, Shapes.box(0.875, 0.5625, 0.25, 1.0, 0.8125, 0.375), BooleanOp.OR)
        shape = Shapes.join(shape, Shapes.box(0.875, 0.5625, 0.625, 1.0, 0.8125, 0.75), BooleanOp.OR)
        shape = Shapes.join(shape, Shapes.box(0.625, 0.5625, 0.875, 0.75, 0.8125, 1.0), BooleanOp.OR)
        shape = Shapes.join(shape, Shapes.box(0.25, 0.5625, 0.875, 0.375, 0.8125, 1.0), BooleanOp.OR)

        return shape
    }
}