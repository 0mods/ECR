package team._0mods.ecr.common.blocks

import net.minecraft.core.BlockPos
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import ru.hollowhorizon.hc.common.utils.literal
import team._0mods.ecr.api.block.client.LowSizeBreakParticle
import team._0mods.ecr.api.utils.simpleTicker
import team._0mods.ecr.common.api.PropertiedEntityBlock
import team._0mods.ecr.common.blocks.entity.ColdDistillerEntity

class ColdDistiller(properties: Properties): PropertiedEntityBlock(properties), LowSizeBreakParticle {
    override fun newBlockEntity(
        pos: BlockPos,
        state: BlockState
    ): BlockEntity = ColdDistillerEntity(pos, state)

    override fun <T : BlockEntity?> getTicker(
        level: Level,
        state: BlockState,
        blockEntityType: BlockEntityType<T?>
    ): BlockEntityTicker<T> = simpleTicker<T, ColdDistillerEntity>(ColdDistillerEntity::tick)

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
