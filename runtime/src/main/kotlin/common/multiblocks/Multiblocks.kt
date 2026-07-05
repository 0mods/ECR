package com.algorithmlx.ecr.common.multiblocks

import com.algorithmlx.ecr.api.multiblock.Multiblock
import com.algorithmlx.ecr.common.init.registry.BlockRegistry

object MithrilineFurnaceMultiblock: Multiblock(5, 5, 3, {
    val a = block(BlockRegistry.instance.mithrilinePlating.defaultBlockState())
    val b = block(BlockRegistry.instance.mithrilineFurnace.defaultBlockState())

    pattern(
        a, null, a, null, a,
        null, a, a, a, null,
        a, a, null, a, a,
        null, a, a, a, null,
        a, null, a, null, a,

        a, null, a, null, a,
        null, null, null, null, null,
        a, null, b, null, a,
        null, null, null, null, null,
        a, null, a, null, a,

        a, null, null, null, a,
        null, null, null, null, null,
        null, null, null, null, null,
        null, null, null, null, null,
        a, null, null, null, a,
    )
})
