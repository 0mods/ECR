package team._0mods.ecr.common.blocks

import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import team._0mods.ecr.common.blocks.entity.MithrilineFurnaceEntity
import team._0mods.ecr.common.init.registry.ECMultiblocks

class MithrilineFurnace(properties: Properties) : BaseEntityBlock(properties) {
    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity = MithrilineFurnaceEntity(pos, state)

    override fun <T : BlockEntity> getTicker(
        level: Level,
        state: BlockState,
        blockEntityType: BlockEntityType<T>
    ): BlockEntityTicker<T> {
        return if (level.isClientSide)
            BlockEntityTicker<T> { l, bp, s, e -> MithrilineFurnaceEntity.onClientTick(l, bp, s, e as MithrilineFurnaceEntity) }
        else BlockEntityTicker<T> { l, bp, s, e -> MithrilineFurnaceEntity.onServerTick(l, bp, s, e as MithrilineFurnaceEntity) }
    }

    @Suppress("override_deprecation", "DEPRECATION")
    override fun use(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hand: InteractionHand,
        hit: BlockHitResult
    ): InteractionResult {
        if (ECMultiblocks.mithrilineFurnace.isComplete(level, pos)) {
            player.displayClientMessage(Component.literal("MB is complete"), false)
        } else player.displayClientMessage(Component.literal("MultiBlock is not complete"), false)

        return super.use(state, level, pos, player, hand, hit)
    }
}