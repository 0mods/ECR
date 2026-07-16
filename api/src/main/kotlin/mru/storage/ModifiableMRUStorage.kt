package com.algorithmlx.ecr.api.mru.storage

interface ModifiableMRUStorage: MRUStorage {
    fun set(amount: Int)

    fun extract(amount: Int): Int

    fun insert(amount: Int): Int

    fun canExtract(max: Int): Boolean = mru - max >= 0

    fun canReceive(receive: Int): Boolean = mru + receive <= mruCapacity

    fun canExtractAndReceive(receiver: ModifiableMRUStorage, max: Int): Boolean {
        if (receiver.canReceive(max) && this.canExtract(max)) {
            this.extract(max)
            receiver.insert(max)
            return true
        }

        return false
    }
}
