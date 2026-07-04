package com.algorithmlx.ecr.common.init

import com.algorithmlx.ecr.common.api.ClusterBlock
import com.algorithmlx.ecr.common.block.CrystalBlock
import com.algorithmlx.ecr.common.block.SolarPrism
import com.mojang.serialization.MapCodec

interface BlockCodecRegistry {
    val solarPrismCodec: MapCodec<SolarPrism>
    val clusterBlockCodec: MapCodec<ClusterBlock>
    val crystalBlockCodec: MapCodec<CrystalBlock>

    companion object {
        @JvmStatic
        val instance: BlockCodecRegistry = UnionRegistry.instance
    }
}
