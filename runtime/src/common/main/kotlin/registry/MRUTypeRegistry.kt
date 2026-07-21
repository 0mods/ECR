package com.algorithmlx.ecr.registry

import com.algorithmlx.ecr.api.mru.MRUType

expect object MRUTypeRegistry {
    val espe: MRUType
    val radiationUnit: MRUType
    val ubmru: MRUType
}
