package com.algorithmlx.ecr.common.init

import com.algorithmlx.ecr.common.block.MithrilineFurnace

interface BlockRegistry {
    val mithrilineFurnace: MithrilineFurnace

    companion object {
        @JvmStatic
        val instance: BlockRegistry = UnionRegistry.instance
    }
}
