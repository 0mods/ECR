package com.algorithmlx.ecr.registry

import com.algorithmlx.ecr.api.utils.ecRL
import com.algorithmlx.ecr.api.registries.ECRegistryKeys
import com.algorithmlx.ecr.api.research.BookType
import com.algorithmlx.ecr.common.components.BoundGemComponent
import com.algorithmlx.ecr.common.components.SoulStoneComponent
import com.algorithmlx.ecr.common.init.ECRModIDs
import net.minecraft.core.Registry
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceKey

@Suppress("ACTUAL_WITHOUT_EXPECT", "unused")
actual object DataComponentRegistry {
    actual val soulStone: DataComponentType<SoulStoneComponent> = register(
        ECRModIDs.SOUL_STONE,
        DataComponentType.builder<SoulStoneComponent>()
            .persistent(SoulStoneComponent.CODEC)
            .networkSynchronized(SoulStoneComponent.STREAM_CODEC)
            .build()
    )
    actual val bookType: DataComponentType<ResourceKey<BookType>> = register(
        ECRModIDs.BOOK_TYPE, DataComponentType.builder<ResourceKey<BookType>>()
            .persistent(ResourceKey.codec(ECRegistryKeys.BOOK_TYPE_KEY))
            .networkSynchronized(ResourceKey.streamCodec(ECRegistryKeys.BOOK_TYPE_KEY))
            .build()
    )
    actual val boundGem: DataComponentType<BoundGemComponent> = register(
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