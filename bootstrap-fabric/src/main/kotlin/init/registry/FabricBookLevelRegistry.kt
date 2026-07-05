package com.algorithmlx.ecr.fabric.init.registry

import com.algorithmlx.ecr.api.ModId
import com.algorithmlx.ecr.api.ecRL
import com.algorithmlx.ecr.api.registries.ECRegistries
import com.algorithmlx.ecr.api.research.BookLevel
import com.algorithmlx.ecr.common.init.registry.BookLevelRegistry
import net.minecraft.core.Registry
import net.minecraft.network.chat.Component

object FabricBookLevelRegistry : BookLevelRegistry {
    override val basic = register("basic", simple(Component.translatable("book_level.$ModId.basic"), 0))

    private fun <T : BookLevel> register(id: String, level: T): T = Registry.register(ECRegistries.BOOK_LEVEL, id.ecRL, level)

    private fun simple(displayName: Component, levelOrder: Int) = object : BookLevel {
        override val name = displayName
        override val order = levelOrder
    }
}
