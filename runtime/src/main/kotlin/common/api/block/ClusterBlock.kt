package com.algorithmlx.ecr.common.api.block

import com.algorithmlx.ecr.common.init.registry.BlockCodecRegistry
import com.mojang.serialization.MapCodec
import net.minecraft.world.level.block.Block

open class ClusterBlock(properties: Properties) : Block(properties) {
    override fun codec(): MapCodec<out Block> = BlockCodecRegistry.instance.clusterBlock
}