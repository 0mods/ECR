package com.algorithmlx.ecr.neoforge.init.registry

import com.algorithmlx.ecr.api.ModId
import com.algorithmlx.ecr.api.mru.MRUType
import com.algorithmlx.ecr.api.registries.ECRegistries
import com.algorithmlx.ecr.common.init.ECRModIDs
import com.algorithmlx.ecr.registry.MRUTypeRegistry
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister

class NeoForgeMRUTypeRegistry(bus: IEventBus): MRUTypeRegistry {
    private val mruTypes = DeferredRegister.create(ECRegistries.MRU_TYPE, ModId)

    init {
        mruTypes.register(bus)
    }

    override val espe: MRUType by register(ECRModIDs.ESPE) { MRUType() }
    override val radiationUnit: MRUType by register(ECRModIDs.MRU) { MRUType() }
    override val ubmru: MRUType by register(ECRModIDs.UBMRU) { MRUType(radiationUnit, 10) }

    private fun register(id: String, factory: () -> MRUType) = mruTypes.register(id) { _ -> factory() }
}
