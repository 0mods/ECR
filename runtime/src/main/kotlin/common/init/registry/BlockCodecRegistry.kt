package com.algorithmlx.ecr.common.init.registry

import com.algorithmlx.ecr.common.api.block.ClusterBlock
import com.algorithmlx.ecr.common.block.ColdDistiller
import com.algorithmlx.ecr.common.block.CrystalBlock
import com.algorithmlx.ecr.common.block.Envoyer
import com.algorithmlx.ecr.common.block.MatrixDestructor
import com.algorithmlx.ecr.common.block.MithrilineFurnace
import com.algorithmlx.ecr.common.block.SolarPrism
import com.mojang.serialization.MapCodec

interface BlockCodecRegistry {
    val solarPrism: MapCodec<SolarPrism>
    val clusterBlock: MapCodec<ClusterBlock>
    val crystalBlock: MapCodec<CrystalBlock>
    val mithrilineFurnace: MapCodec<MithrilineFurnace>
    val envoyer: MapCodec<Envoyer>
    val matrixDestructor: MapCodec<MatrixDestructor>
    val coldDistiller: MapCodec<ColdDistiller>

    companion object {
        lateinit var instance: BlockCodecRegistry
    }
}
