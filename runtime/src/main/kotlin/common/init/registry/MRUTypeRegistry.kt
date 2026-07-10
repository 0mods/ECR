package com.algorithmlx.ecr.common.init.registry

import com.algorithmlx.ecr.api.mru.MRUType

interface MRUTypeRegistry {
    val espe: MRUType
    val radiationUnit: MRUType
    val ubmru: MRUType

    companion object { @JvmStatic lateinit var instance: MRUTypeRegistry }
}
