package com.algorithmlx.ecr.registry

import com.algorithmlx.ecr.api.utils.ecRL
import com.algorithmlx.ecr.api.mru.MRUType
import com.algorithmlx.ecr.api.registries.ECRegistries
import com.algorithmlx.ecr.common.init.ECRModIDs
import net.minecraft.core.Registry

@Suppress("ACTUAL_WITHOUT_EXPECT", "unused")
actual object MRUTypeRegistry {
    actual val espe: MRUType = register(ECRModIDs.ESPE, MRUType())
    actual val radiationUnit: MRUType = register(ECRModIDs.MRU, MRUType())
    actual val ubmru: MRUType = register(ECRModIDs.UBMRU, MRUType(radiationUnit, 10))

    private fun <T: MRUType> register(id: String, type: T) = Registry.register(ECRegistries.MRU_TYPE, id.ecRL, type)
}