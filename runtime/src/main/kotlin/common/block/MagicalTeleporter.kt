package com.algorithmlx.ecr.common.block

import com.algorithmlx.ecr.api.utils.checkAndOpenMenu
import com.algorithmlx.ecr.api.utils.simpleTicker
import com.algorithmlx.ecr.common.block.entity.MagicalTeleporterEntity
import net.minecraft.core.BlockPos
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult

class MagicalTeleporter(properties: Properties) : Block(properties), EntityBlock {
    override fun newBlockEntity(
        worldPosition: BlockPos,
        blockState: BlockState
    ): BlockEntity {
        return MagicalTeleporterEntity(worldPosition, blockState)
    }

    override fun <T : BlockEntity> getTicker(
        level: Level,
        blockState: BlockState,
        type: BlockEntityType<T>
    ): BlockEntityTicker<T> = simpleTicker<T, MagicalTeleporterEntity> { level, pos, _, blockEntity ->
        MagicalTeleporterEntity.onTick(level, pos, blockEntity)
    }

    override fun useWithoutItem(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hitResult: BlockHitResult
    ): InteractionResult = if (MagicalTeleporterEntity.hasValidStructure(level, pos))
        checkAndOpenMenu<MagicalTeleporterEntity>(player, level, pos)
    else InteractionResult.FAIL
}
