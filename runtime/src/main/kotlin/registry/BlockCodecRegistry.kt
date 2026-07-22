package com.algorithmlx.ecr.registry

import com.algorithmlx.ecr.common.api.block.ClusterBlock
import com.algorithmlx.ecr.common.block.ColdDistiller
import com.algorithmlx.ecr.common.block.CrystalBlock
import com.algorithmlx.ecr.common.block.MagicTable
import com.algorithmlx.ecr.common.block.MatrixDestructor
import com.algorithmlx.ecr.common.block.MithrilineFurnace
import com.algorithmlx.ecr.common.block.SolarPrism
import com.mojang.serialization.MapCodec

interface BlockCodecRegistry {
    val solarPrism: MapCodec<SolarPrism>
    val clusterBlock: MapCodec<ClusterBlock>
    val crystalBlock: MapCodec<CrystalBlock>
    val mithrilineFurnace: MapCodec<MithrilineFurnace>
    val magicTable: MapCodec<MagicTable>
    val matrixDestructor: MapCodec<MatrixDestructor>
    val coldDistiller: MapCodec<ColdDistiller>

    companion object {
        @JvmStatic
        lateinit var instance: BlockCodecRegistry
    }
}
