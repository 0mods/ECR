package com.algorithmlx.ecr.neoforge.init.registry

import com.algorithmlx.ecr.api.ModId
import com.algorithmlx.ecr.api.registries.ECRegistryKeys
import com.algorithmlx.ecr.api.research.BookType
import com.algorithmlx.ecr.common.components.BoundGemComponent
import com.algorithmlx.ecr.common.components.PlayerMatrixComponent
import com.algorithmlx.ecr.common.components.SoulStoneComponent
import com.algorithmlx.ecr.common.init.ECRModIDs
import com.algorithmlx.ecr.registry.DataComponentRegistry
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister

class NeoForgeDataComponentRegistry(bus: IEventBus): DataComponentRegistry {
    private val dataComponents = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, ModId)

    init {
        dataComponents.register(bus)
    }

    override val soulStone: DataComponentType<SoulStoneComponent> by register<SoulStoneComponent>(ECRModIDs.SOUL_STONE) { builder ->
        builder.persistent(SoulStoneComponent.CODEC).networkSynchronized(SoulStoneComponent.STREAM_CODEC)
    }

    override val bookType: DataComponentType<ResourceKey<BookType>> by register<ResourceKey<BookType>>(ECRModIDs.BOOK_TYPE) { builder ->
        builder.persistent(ResourceKey.codec(ECRegistryKeys.BOOK_TYPE_KEY)).networkSynchronized(ResourceKey.streamCodec(ECRegistryKeys.BOOK_TYPE_KEY))
    }

    override val boundGem: DataComponentType<BoundGemComponent> by register<BoundGemComponent>(ECRModIDs.BOUND_GEM) { builder ->
        builder.persistent(BoundGemComponent.CODEC).networkSynchronized(BoundGemComponent.STREAM_CODEC)
    }

    override val playerMatrix: DataComponentType<PlayerMatrixComponent> by register<PlayerMatrixComponent>(ECRModIDs.PLAYER_MATRIX) { builder ->
        builder.persistent(PlayerMatrixComponent.CODEC).networkSynchronized(PlayerMatrixComponent.STREAM_CODEC)
    }

    private fun <T: Any> register(id: String, configure: (DataComponentType.Builder<T>) -> DataComponentType.Builder<T>) = dataComponents.registerComponentType<T>(id) { builder -> configure(builder) }
}
