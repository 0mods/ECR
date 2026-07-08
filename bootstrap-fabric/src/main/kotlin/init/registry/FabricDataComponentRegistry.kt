package com.algorithmlx.ecr.fabric.init.registry

import com.algorithmlx.ecr.api.ecRL
import com.algorithmlx.ecr.api.research.BookType
import com.algorithmlx.ecr.common.components.BoundGemComponent
import com.algorithmlx.ecr.common.components.SoulStoneComponent
import com.algorithmlx.ecr.common.init.ECRModIDs
import com.algorithmlx.ecr.common.init.registry.DataComponentRegistry
import net.minecraft.core.Registry
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.registries.BuiltInRegistries

object FabricDataComponentRegistry: DataComponentRegistry {
    override val soulStone: DataComponentType<SoulStoneComponent> = register(
        ECRModIDs.SOUL_STONE,
        DataComponentType.builder<SoulStoneComponent>()
            .persistent(SoulStoneComponent.codec)
            .networkSynchronized(SoulStoneComponent.codecStream)
            .build()
    )
    override val bookType: DataComponentType<BookType> = register(
        ECRModIDs.BOOK_TYPE, DataComponentType.builder<BookType>()
            .persistent(BookType.codec)
            .networkSynchronized(BookType.codecStream)
            .build()
    )
    override val boundGem: DataComponentType<BoundGemComponent> = register(
        ECRModIDs.BOUND_GEM,
        DataComponentType.builder<BoundGemComponent>()
            .persistent(BoundGemComponent.codec)
            .networkSynchronized(BoundGemComponent.streamCodec)
            .build()
    )

    private fun <T: DataComponentType<*>> register(id: String, component: T): T = Registry.register(
        BuiltInRegistries.DATA_COMPONENT_TYPE,
        id.ecRL,
        component
    )
}
