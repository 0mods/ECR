package com.algorithmlx.ecr.common.api

import com.algorithmlx.ecr.common.init.Registry
import com.mojang.serialization.MapCodec
import net.minecraft.world.level.block.Block

open class ClusterBlock(properties: Properties) : Block(properties) {
    override fun codec(): MapCodec<out Block> = Registry.instance.clusterBlockCodec
}