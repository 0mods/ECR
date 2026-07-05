package com.algorithmlx.ecr.common.init.registry

import com.algorithmlx.ecr.common.item.SoulStone

interface ItemRegistry {
    val soulStone: SoulStone

    companion object {
        lateinit var instance: ItemRegistry
    }
}
