package com.algorithmlx.ecr.registry

import com.algorithmlx.ecr.api.ModId
import com.algorithmlx.ecr.api.registries.ECRegistryKeys
import com.algorithmlx.ecr.api.research.BookType
import com.algorithmlx.ecr.common.components.BoundGemComponent
import com.algorithmlx.ecr.common.components.SoulStoneComponent
import com.algorithmlx.ecr.common.init.ECRModIDs
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister

@Suppress("ACTUAL_WITHOUT_EXPECT", "unused")
actual object DataComponentRegistry {
    private val dataComponents = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, ModId)

    fun init(bus: IEventBus) {
        dataComponents.register(bus)
    }

    private val soulStoneComponent = dataComponents.registerComponentType(ECRModIDs.SOUL_STONE) { builder ->
        builder.persistent(SoulStoneComponent.CODEC).networkSynchronized(SoulStoneComponent.STREAM_CODEC)
    }

    private val bookTypeComponent = dataComponents.registerComponentType(ECRModIDs.BOOK_TYPE) { builder ->
        builder
            .persistent(ResourceKey.codec(ECRegistryKeys.BOOK_TYPE_KEY))
            .networkSynchronized(ResourceKey.streamCodec(ECRegistryKeys.BOOK_TYPE_KEY))
    }

    private val boundGemComponent = dataComponents.registerComponentType(ECRModIDs.BOUND_GEM) { builder ->
        builder.persistent(BoundGemComponent.CODEC).networkSynchronized(BoundGemComponent.STREAM_CODEC)
    }

    actual val soulStone: DataComponentType<SoulStoneComponent> by lazy { soulStoneComponent.get() }
    actual val bookType: DataComponentType<ResourceKey<BookType>> by lazy { bookTypeComponent.get() }
    actual val boundGem: DataComponentType<BoundGemComponent> by lazy { boundGemComponent.get() }
}
