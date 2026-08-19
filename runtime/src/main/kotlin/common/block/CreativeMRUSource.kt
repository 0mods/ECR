package com.algorithmlx.ecr.common.block

import com.algorithmlx.ecr.common.block.entity.CreativeMRUSourceEntity
import com.algorithmlx.ecr.registry.BlockCodecRegistry
import com.mojang.serialization.MapCodec
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState

class CreativeMRUSource(
    properties: Properties,
) : Block(properties),
    EntityBlock {
    override fun codec(): MapCodec<out Block> = BlockCodecRegistry.instance.creativeMRUSource

    override fun newBlockEntity(
        worldPosition: BlockPos,
        blockState: BlockState,
    ): BlockEntity = CreativeMRUSourceEntity(worldPosition, blockState)
}
