package team._0mods.ecr.common.blocks

import net.minecraft.core.BlockPos
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import team._0mods.ecr.api.block.client.LowSizeBreakParticle
import team._0mods.ecr.common.api.PropertiedEntityBlock

class ColdDistiller(properties: Properties) : PropertiedEntityBlock(properties), LowSizeBreakParticle {
    override fun newBlockEntity(
        pos: BlockPos,
        state: BlockState
    ): BlockEntity? {
        TODO("Not yet implemented")
    }

    override fun use(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hand: InteractionHand,
        hit: BlockHitResult
    ): InteractionResult? {
        return super.use(state, level, pos, player, hand, hit)
    }

    override fun onRemove(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        newState: BlockState,
        movedByPiston: Boolean
    ) {
        super.onRemove(state, level, pos, newState, movedByPiston)
    }
}
