package com.algorithmlx.ecr.neoforge.init.registry

import com.algorithmlx.ecr.api.ModId
import com.algorithmlx.ecr.api.mru.MRUType
import com.algorithmlx.ecr.api.registries.ECRegistries
import com.algorithmlx.ecr.common.init.ECRModIDs
import com.algorithmlx.ecr.registry.MRUTypeRegistry
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister

object NeoForgeMRUTypeRegistry : MRUTypeRegistry {
    private val mruTypes = DeferredRegister.create(ECRegistries.MRU_TYPE, ModId)

    fun init(bus: IEventBus) {
        mruTypes.register(bus)
    }

    private val espeType = mruTypes.register(ECRModIDs.ESPE) { _ -> MRUType() }
    private val radiationUnitType = mruTypes.register(ECRModIDs.MRU) { _ -> MRUType() }
    private val umbruType = mruTypes.register(ECRModIDs.UBMRU) { _ -> MRUType(radiationUnit, 10) }

    override val espe: MRUType by lazy { espeType.get() }
    override val radiationUnit: MRUType by lazy { radiationUnitType.get() }
    override val ubmru: MRUType by lazy { umbruType.get() }
}
