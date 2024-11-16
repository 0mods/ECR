package team._0mods.ecr.common.blocks

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import net.minecraftforge.network.NetworkHooks
import team._0mods.ecr.api.block.checkAndOpenMenu
import team._0mods.ecr.api.block.client.LowSizeBreakParticle
import team._0mods.ecr.api.block.prepareDrops
import team._0mods.ecr.api.block.simpleTicker
import team._0mods.ecr.common.api.PropertiedEntityBlock
import team._0mods.ecr.common.blocks.entity.XLikeBlockEntity
import team._0mods.ecr.common.init.registry.ECRegistry

class MagicTable(properties: Properties): PropertiedEntityBlock(properties), LowSizeBreakParticle {
    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity = XLikeBlockEntity.MagicTable(pos, state)

    override fun <T : BlockEntity?> getTicker(
        level: Level,
        state: BlockState,
        blockEntityType: BlockEntityType<T>
    ): BlockEntityTicker<T> = simpleTicker<T, XLikeBlockEntity.MagicTable>(XLikeBlockEntity.MagicTable::onTick)

    override fun use(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hand: InteractionHand,
        hit: BlockHitResult
    ): InteractionResult {
        if (!level.isClientSide) {
            val be = level.getBlockEntity(pos, ECRegistry.magicTableEntity.get())
            if (be.isPresent) {
                NetworkHooks.openScreen(player as ServerPlayer, be.get(), be.get().blockPos)
            }
        }
        return InteractionResult.SUCCESS
    }

    override fun onRemove(state: BlockState, level: Level, pos: BlockPos, newState: BlockState, isMoving: Boolean) {
        prepareDrops<XLikeBlockEntity.MagicTable>(state, level, pos, newState)

        super.onRemove(state, level, pos, newState, isMoving)
    }
}