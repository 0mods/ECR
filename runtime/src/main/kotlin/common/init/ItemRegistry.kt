package com.algorithmlx.ecr.common.init

import com.algorithmlx.ecr.common.item.SoulStone

interface ItemRegistry {
    val soulStone: SoulStone

    companion object {
        @JvmStatic
        val instance: ItemRegistry = UnionRegistry.instance
    }
}
