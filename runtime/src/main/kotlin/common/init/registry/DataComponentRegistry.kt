package com.algorithmlx.ecr.common.init.registry

import com.algorithmlx.ecr.api.research.BookType
import com.algorithmlx.ecr.common.components.BoundGemComponent
import com.algorithmlx.ecr.common.components.SoulStoneComponent
import net.minecraft.core.component.DataComponentType

interface DataComponentRegistry {
    val soulStone: DataComponentType<SoulStoneComponent>
    val bookType: DataComponentType<BookType>
    val boundGem: DataComponentType<BoundGemComponent>

    companion object {
        lateinit var instance: DataComponentRegistry
    }
}
