package com.algorithmlx.ecr.registry

import com.algorithmlx.ecr.api.assembled.AssembledMultiblockDefinition
import com.algorithmlx.ecr.api.multiblock.Multiblock

interface MultiblockRegistry {
    val mithrilineFurnace: Multiblock
    val soulStone: Multiblock
    val flameCrystal: Multiblock
    val waterCrystal: Multiblock
    val earthCrystal: Multiblock
    val airCrystal: Multiblock
    val lightningCollector: Multiblock
    val enrichmentChamber: Multiblock
    val rayTower: AssembledMultiblockDefinition

    companion object {
        @JvmStatic
        lateinit var instance: MultiblockRegistry
    }
}
