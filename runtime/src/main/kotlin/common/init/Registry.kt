package com.algorithmlx.ecr.common.init

import com.algorithmlx.ecr.common.api.ClusterBlock
import com.algorithmlx.ecr.common.blocks.CrystalBlock
import com.algorithmlx.ecr.common.blocks.SolarPrism
import com.mojang.serialization.MapCodec

interface Registry {
    val solarPrismCodec: MapCodec<SolarPrism>
    val clusterBlockCodec: MapCodec<ClusterBlock>
    val crystalBlockCodec: MapCodec<CrystalBlock>

    companion object {
        lateinit var instance: Registry
    }
}
