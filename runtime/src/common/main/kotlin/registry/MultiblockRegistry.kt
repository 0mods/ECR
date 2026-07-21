package com.algorithmlx.ecr.registry

import com.algorithmlx.ecr.api.multiblock.Multiblock

interface MultiblockRegistry {
    val mithrilineFurnace: Multiblock
    val soulStone: Multiblock
    val flameCrystal: Multiblock
    val waterCrystal: Multiblock
    val earthCrystal: Multiblock
    val airCrystal: Multiblock
    val lightningCollector: Multiblock

    companion object {
        lateinit var instance: MultiblockRegistry
    }
}
