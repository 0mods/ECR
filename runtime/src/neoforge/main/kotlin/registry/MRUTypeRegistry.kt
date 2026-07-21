package com.algorithmlx.ecr.registry

import com.algorithmlx.ecr.api.ModId
import com.algorithmlx.ecr.api.mru.MRUType
import com.algorithmlx.ecr.api.registries.ECRegistries
import com.algorithmlx.ecr.common.init.ECRModIDs
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister

@Suppress("ACTUAL_WITHOUT_EXPECT", "unused")
actual object MRUTypeRegistry {
    private val mruTypes = DeferredRegister.create(ECRegistries.MRU_TYPE, ModId)

    fun init(bus: IEventBus) {
        mruTypes.register(bus)
    }

    private val espeType = mruTypes.register(ECRModIDs.ESPE) { _ -> MRUType() }
    private val radiationUnitType = mruTypes.register(ECRModIDs.MRU) { _ -> MRUType() }
    private val umbruType = mruTypes.register(ECRModIDs.UBMRU) { _ -> MRUType(radiationUnit, 10) }

    actual val espe: MRUType by lazy { espeType.get() }
    actual val radiationUnit: MRUType by lazy { radiationUnitType.get() }
    actual val ubmru: MRUType by lazy { umbruType.get() }
}
