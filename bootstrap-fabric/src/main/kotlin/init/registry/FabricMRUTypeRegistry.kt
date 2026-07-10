package com.algorithmlx.ecr.fabric.init.registry

import com.algorithmlx.ecr.api.ecRL
import com.algorithmlx.ecr.api.mru.MRUType
import com.algorithmlx.ecr.api.registries.ECRegistries
import com.algorithmlx.ecr.common.init.ECRModIDs
import com.algorithmlx.ecr.common.init.registry.MRUTypeRegistry
import net.minecraft.core.Registry
import net.minecraft.network.chat.Component

object FabricMRUTypeRegistry: MRUTypeRegistry {
    override val espe: MRUType = register(ECRModIDs.ESPE, MRUType())
    override val radiationUnit: MRUType = register(ECRModIDs.MRU, MRUType())
    override val ubmru: MRUType = register(ECRModIDs.UBMRU, MRUType(radiationUnit, 10))

    private fun <T: MRUType> register(id: String, type: T) = Registry.register(ECRegistries.MRU_TYPE, id.ecRL, type)
}
