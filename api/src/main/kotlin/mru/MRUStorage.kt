package com.algorithmlx.ecr.api.mru

interface MRUStorage {
    val mru: Int

    val mruCapacity: Int

    val mruType: MRUType

    val isFilled: Boolean get() = mru == mruCapacity

    val hasMRU: Boolean get() = mru > 0

    val isEmpty: Boolean get() = mru == 0
}
