package com.algorithmlx.ecr.neoforge.init.registry

import com.algorithmlx.ecr.api.ModId
import com.algorithmlx.ecr.api.registries.ECRegistries
import com.algorithmlx.ecr.api.research.BookLevel
import com.algorithmlx.ecr.common.init.registry.BookLevelRegistry
import net.minecraft.network.chat.Component
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister

class NeoForgeBookLevelRegistry(bus: IEventBus) : BookLevelRegistry {
    private val levels = DeferredRegister.create(ECRegistries.BOOK_LEVEL, ModId)
    private val basicLevel = levels.register("basic") { _ -> simple(Component.translatable("book_level.$ModId.basic"), 0) }

    init {
        levels.register(bus)
    }

    override val basic: BookLevel by lazy { basicLevel.get() }

    private fun simple(displayName: Component, levelOrder: Int) = object : BookLevel {
        override val name = displayName
        override val order = levelOrder
    }
}
