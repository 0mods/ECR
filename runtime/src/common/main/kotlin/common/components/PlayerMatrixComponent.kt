package com.algorithmlx.ecr.common.components

import com.algorithmlx.ecr.api.mru.MRUType
import com.algorithmlx.ecr.api.mru.storage.MRUStorage
import com.algorithmlx.ecr.registry.MRUTypeRegistry

class PlayerMatrixComponent(override val mru: Int): MRUStorage {
    override val mruCapacity: Int = -1
    override val mruType: MRUType = MRUTypeRegistry.instance.ubmru

    override val isFilled: Boolean = false
}
