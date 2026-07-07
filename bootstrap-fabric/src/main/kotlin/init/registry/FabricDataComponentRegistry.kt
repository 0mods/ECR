package com.algorithmlx.ecr.fabric.init.registry

import com.algorithmlx.ecr.api.ecRL
import com.algorithmlx.ecr.api.research.BookType
import com.algorithmlx.ecr.common.components.SoulStoneComponent
import com.algorithmlx.ecr.common.init.registry.DataComponentRegistry
import net.minecraft.core.Registry
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.registries.BuiltInRegistries

object FabricDataComponentRegistry: DataComponentRegistry {
    override val soulStone: DataComponentType<SoulStoneComponent> = register(
        "soul_stone",
        DataComponentType.builder<SoulStoneComponent>()
            .persistent(SoulStoneComponent.codec)
            .networkSynchronized(SoulStoneComponent.codecStream)
            .build()
    )
    override val bookType: DataComponentType<BookType> = register(
        "book_type", DataComponentType.builder<BookType>()
            .persistent(BookType.codec)
            .networkSynchronized(BookType.codecStream)
            .build()
    )

    private fun <T: DataComponentType<*>> register(id: String, component: T): T = Registry.register(
        BuiltInRegistries.DATA_COMPONENT_TYPE,
        id.ecRL,
        component
    )
}
