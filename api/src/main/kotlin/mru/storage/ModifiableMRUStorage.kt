package com.algorithmlx.ecr.api.mru.storage

interface ModifiableMRUStorage: MRUStorage {
    fun set(amount: Int)

    fun extract(amount: Int): Int

    fun insert(amount: Int): Int
}
