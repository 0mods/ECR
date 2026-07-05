package com.algorithmlx.ecr.common.init.registry

import com.algorithmlx.ecr.common.api.ClusterBlock
import com.algorithmlx.ecr.common.block.CrystalBlock
import com.algorithmlx.ecr.common.block.MithrilineFurnace
import com.algorithmlx.ecr.common.block.SolarPrism
import com.mojang.serialization.MapCodec

interface BlockCodecRegistry {
    val solarPrism: MapCodec<SolarPrism>
    val clusterBlock: MapCodec<ClusterBlock>
    val crystalBlock: MapCodec<CrystalBlock>
    val mithrilineFurnace: MapCodec<MithrilineFurnace>

    companion object {
        lateinit var instance: BlockCodecRegistry
    }
}
