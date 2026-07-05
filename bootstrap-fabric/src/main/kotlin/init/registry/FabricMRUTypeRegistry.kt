package com.algorithmlx.ecr.fabric.init.registry

import com.algorithmlx.ecr.api.ecRL
import com.algorithmlx.ecr.api.mru.MRUType
import com.algorithmlx.ecr.api.registries.ECRegistries
import com.algorithmlx.ecr.common.init.registry.MRUTypeRegistry
import net.minecraft.core.Registry
import net.minecraft.network.chat.Component

object FabricMRUTypeRegistry: MRUTypeRegistry {
    override val espe: MRUType = register("espe", simple(Component.literal("ESPE")))
    override val radiationUnit: MRUType = register("mru", simple(Component.literal("MRU")))

    private fun <T: MRUType> register(id: String, type: T) = Registry.register(ECRegistries.MRU_TYPE, id.ecRL, type)

    private fun simple(display: Component) = object : MRUType {
        override val name: Component = display
    }
}