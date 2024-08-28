package team._0mods.ecr.common.blocks

import net.minecraft.core.BlockPos
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
import team._0mods.ecr.api.block.checkAndOpenMenu
import team._0mods.ecr.api.block.prepareDrops
import team._0mods.ecr.api.block.simpleTicker
import team._0mods.ecr.common.blocks.entity.EnvoyerBlockEntity

class Envoyer(properties: Properties) : BaseEntityBlock(properties) {
    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity = EnvoyerBlockEntity(pos, state)

    override fun <T : BlockEntity?> getTicker(
        level: Level,
        state: BlockState,
        blockEntityType: BlockEntityType<T>
    ): BlockEntityTicker<T> = simpleTicker<T, EnvoyerBlockEntity>(EnvoyerBlockEntity::onTick)

    override fun use(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hand: InteractionHand,
        hit: BlockHitResult
    ): InteractionResult {
        return checkAndOpenMenu<EnvoyerBlockEntity>(player, level, pos)
    }

    override fun onRemove(state: BlockState, level: Level, pos: BlockPos, newState: BlockState, isMoving: Boolean) {
        prepareDrops<EnvoyerBlockEntity>(state, level, pos, newState)

        super.onRemove(state, level, pos, newState, isMoving)
    }

    override fun getRenderShape(state: BlockState): RenderShape = RenderShape.MODEL
}