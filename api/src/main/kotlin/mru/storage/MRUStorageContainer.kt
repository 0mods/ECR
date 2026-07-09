package com.algorithmlx.ecr.api.mru.storage

import com.algorithmlx.ecr.api.mru.MRUType
import kotlinx.coroutines.internal.SynchronizedObject
import kotlinx.coroutines.internal.synchronized
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput
import kotlin.math.max
import kotlin.math.min

data class MRUStorageContainer(
    override val mruCapacity: Int,
    override val mruType: MRUType,
    val onChange: (Int) -> Unit = {}
): IOMRUStorage {
    private var mru0 = 0

    override val mru: Int
        get() = mru0

    override fun set(amount: Int) {
        val newAmount = amount.coerceIn(0, this.mruCapacity)
        if (this.mru0 == newAmount) return

        val prev = this.mru0
        this.mru0 = newAmount
        this.onChange(prev)
    }

    override fun extract(amount: Int): Int {
        if (amount <= 0) return 0

        val extracted = this.mru0.coerceAtMost(amount)
        if (extracted <= 0) return 0

        val prev = this.mru0
        this.mru0 -= extracted
        this.onChange(prev)

        return extracted
    }

    override fun insert(amount: Int): Int {
        if (amount <= 0) return 0

        val inserted = (this.mruCapacity - this.mru0).coerceAtMost(amount)
        if (inserted <= 0) return 0

        val prev = this.mru0
        this.mru0 += inserted
        this.onChange(prev)

        return inserted
    }

    override fun save(output: ValueOutput) {
        output.putInt("mru", this.mru0)
    }

    override fun load(input: ValueInput) {
        this.mru0 = input.getIntOr("mru", 0).coerceIn(0, this.mruCapacity)
    }
}
