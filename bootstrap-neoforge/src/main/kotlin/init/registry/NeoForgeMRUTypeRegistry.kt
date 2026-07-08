package com.algorithmlx.ecr.neoforge.init.registry

import com.algorithmlx.ecr.api.ModId
import com.algorithmlx.ecr.api.mru.MRUType
import com.algorithmlx.ecr.api.registries.ECRegistries
import com.algorithmlx.ecr.common.init.ECRModIDs
import com.algorithmlx.ecr.common.init.registry.MRUTypeRegistry
import net.minecraft.network.chat.Component
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister

class NeoForgeMRUTypeRegistry(bus: IEventBus): MRUTypeRegistry {
    private val mruTypes = DeferredRegister.create(ECRegistries.MRU_TYPE, ModId)

    init {
        mruTypes.register(bus)
    }

    private val espeType = mruTypes.register(ECRModIDs.ESPE) { _ -> simple(Component.literal("ESPE")) }
    private val radiationUnitType = mruTypes.register(ECRModIDs.MRU) { _ -> simple(Component.literal("MRU")) }

    override val espe: MRUType by lazy { espeType.get() }
    override val radiationUnit: MRUType by lazy { radiationUnitType.get() }

    private fun simple(display: Component) = object : MRUType {
        override val name: Component = display
    }
}
