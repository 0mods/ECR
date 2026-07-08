package com.algorithmlx.ecr.neoforge.init.registry

import com.algorithmlx.ecr.api.ModId
import com.algorithmlx.ecr.api.research.BookType
import com.algorithmlx.ecr.common.components.BoundGemComponent
import com.algorithmlx.ecr.common.components.SoulStoneComponent
import com.algorithmlx.ecr.common.init.registry.DataComponentRegistry
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.registries.Registries
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister

class NeoForgeDataComponentRegistry(bus: IEventBus): DataComponentRegistry {
    private val dataComponents = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, ModId)

    init {
        dataComponents.register(bus)
    }

    private val soulStoneComponent = dataComponents.registerComponentType("soul_stone") { builder ->
        builder.persistent(SoulStoneComponent.codec).networkSynchronized(SoulStoneComponent.codecStream)
    }

    private val bookTypeComponent = dataComponents.registerComponentType("book_type") { builder ->
        builder.persistent(BookType.codec).networkSynchronized(BookType.codecStream)
    }

    private val boundGemComponent = dataComponents.registerComponentType("bound_gem") { builder ->
        builder.persistent(BoundGemComponent.codec).networkSynchronized(BoundGemComponent.streamCodec)
    }

    override val soulStone: DataComponentType<SoulStoneComponent> by lazy { soulStoneComponent.get() }
    override val bookType: DataComponentType<BookType> by lazy { bookTypeComponent.get() }
    override val boundGem: DataComponentType<BoundGemComponent> by lazy { boundGemComponent.get() }
}
