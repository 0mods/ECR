package com.algorithmlx.ecr.common.block

import com.algorithmlx.ecr.api.utils.simpleTicker
import com.algorithmlx.ecr.common.block.entity.ColdDistillerEntity
import com.algorithmlx.ecr.common.init.registry.BlockCodecRegistry
import com.mojang.serialization.MapCodec
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState

class ColdDistiller(properties: Properties): Block(properties), EntityBlock {
    override fun codec(): MapCodec<out Block> = BlockCodecRegistry.instance.coldDistiller

    override fun newBlockEntity(
        worldPosition: BlockPos,
        blockState: BlockState
    ): BlockEntity = ColdDistillerEntity(worldPosition, blockState)

    override fun <T : BlockEntity> getTicker(
        level: Level,
        blockState: BlockState,
        type: BlockEntityType<T>
    ): BlockEntityTicker<T> = simpleTicker<T, ColdDistillerEntity> { level, blockPos, _, blockEntity ->
        ColdDistillerEntity.onTick(level, blockPos, blockEntity)
    }
}
