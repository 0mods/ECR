package com.algorithmlx.ecr.common.init

import com.algorithmlx.ecr.api.multiblock.Multiblock

interface MultiblockRegistry {
    val mithrilineFurnaceMultiblock: Multiblock

    companion object {
        @JvmStatic
        val instance: MultiblockRegistry = UnionRegistry.instance
    }
}
