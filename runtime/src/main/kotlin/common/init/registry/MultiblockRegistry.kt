package com.algorithmlx.ecr.common.init.registry

import com.algorithmlx.ecr.api.multiblock.Multiblock

interface MultiblockRegistry {
    val mithrilineFurnace: Multiblock

    companion object {
        lateinit var instance: MultiblockRegistry
    }
}
