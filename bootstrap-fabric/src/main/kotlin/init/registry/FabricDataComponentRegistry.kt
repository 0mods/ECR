package com.algorithmlx.ecr.fabric.init.registry

import com.algorithmlx.ecr.api.utils.ecRL
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
            .persistent(SoulStoneComponent.CODEC)
            .networkSynchronized(SoulStoneComponent.STREAM_CODEC)
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
            .persistent(BoundGemComponent.CODEC)
            .networkSynchronized(BoundGemComponent.STREAM_CODEC)
            .build()
    )

    private fun <T: DataComponentType<*>> register(id: String, component: T): T = Registry.register(
        BuiltInRegistries.DATA_COMPONENT_TYPE,
        id.ecRL,
        component
    )
}
