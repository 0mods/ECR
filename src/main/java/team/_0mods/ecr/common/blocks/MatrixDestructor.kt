package team._0mods.ecr.common.blocks

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import net.minecraftforge.network.NetworkHooks
import team._0mods.ecr.api.block.MRUGenerator
import team._0mods.ecr.common.blocks.entity.MatrixDestructorEntity

class MatrixDestructor(properties: Properties) : BaseEntityBlock(properties), MRUGenerator {
    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity = MatrixDestructorEntity(pos, state)

    override fun <T : BlockEntity?> getTicker(
        level: Level,
        state: BlockState,
        blockEntityType: BlockEntityType<T>
    ): BlockEntityTicker<T> = BlockEntityTicker { l, bp, s, e ->
        MatrixDestructorEntity.onTick(l, bp, s, e as MatrixDestructorEntity)
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
        if (!level.isClientSide) {
            val be = level.getBlockEntity(pos)
            if (be is MatrixDestructorEntity) {
                NetworkHooks.openScreen(player as ServerPlayer, be, pos)
            } else throw IllegalStateException("Can not open any block entity that is not instanceof MatrixDestructorEntity")
        }

        return InteractionResult.SUCCESS
    }

    override fun getRenderShape(state: BlockState): RenderShape = RenderShape.MODEL
}