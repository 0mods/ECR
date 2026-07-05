package com.algorithmlx.ecr.common.init.registry

import com.algorithmlx.ecr.common.item.SoulStone
import com.algorithmlx.ecr.common.item.ResearchBookItem

interface ItemRegistry {
    val soulStone: SoulStone
    val researchBook: ResearchBookItem

    companion object {
        lateinit var instance: ItemRegistry
    }
}
