package com.algorithmlx.ecr.api.mru.storage

import com.algorithmlx.ecr.api.mru.MRUType
import com.algorithmlx.ecr.api.mru.balance.MRUBalanceContainer
import com.algorithmlx.ecr.api.mru.balance.MutableMRUBalance
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput
import kotlin.jvm.optionals.getOrNull

data class ExtremeMRUStorageContainer(
    override val mruCapacity: Int,
    override val mruType: MRUType,
    val onChange: (Int) -> Unit = {},
): IOMRUStorage {
    override val balance: MutableMRUBalance = MRUBalanceContainer(0.0, 0.0, 0.0, 0.0)

    private var mutableMRU = 0

    override val mru: Int
        get() = mutableMRU

    override fun set(amount: Int) {
        if (this.mutableMRU == amount) return

        val prev = this.mutableMRU
        this.mutableMRU = amount
        this.onChange(prev)
    }

    override fun extract(amount: Int): Int {
        if (amount <= 0) return 0

        val extracted = this.mutableMRU.coerceAtMost(amount)
        if (extracted <= 0) return 0

        val prev = this.mutableMRU
        this.mutableMRU -= extracted
        this.onChange(prev)

        return extracted
    }

    override fun insert(amount: Int): Int {
        if (amount <= 0) return 0

        val inserted = (this.mruCapacity - this.mutableMRU).coerceAtMost(amount)
        if (inserted <= 0) return 0

        val prev = this.mutableMRU
        this.mutableMRU += inserted
        this.onChange(prev)

        return inserted
    }

    override fun save(output: ValueOutput) {
        output.putInt("mru", this.mutableMRU)
        balance.save(output.child("balance"))
    }

    override fun load(input: ValueInput) {
        this.mutableMRU = input.getIntOr("mru", 0).coerceAtLeast(0)
        input.child("balance").getOrNull()?.let { this.balance.load(it) }
    }
}

data class MRUStorageContainer(
    override val mruCapacity: Int,
    override val mruType: MRUType,
    val onChange: (Int) -> Unit = {}
): IOMRUStorage {
    override val balance: MutableMRUBalance = MRUBalanceContainer(0.0, 0.0, 0.0, 0.0)

    private var mutableMRU = 0

    override val mru: Int
        get() = mutableMRU

    override fun set(amount: Int) {
        val newAmount = amount.coerceIn(0, this.mruCapacity)
        if (this.mutableMRU == newAmount) return

        val prev = this.mutableMRU
        this.mutableMRU = newAmount
        this.onChange(prev)
    }

    override fun extract(amount: Int): Int {
        if (amount <= 0) return 0

        val extracted = this.mutableMRU.coerceAtMost(amount)
        if (extracted <= 0) return 0

        val prev = this.mutableMRU
        this.mutableMRU -= extracted
        this.onChange(prev)

        return extracted
    }

    override fun insert(amount: Int): Int {
        if (amount <= 0) return 0

        val inserted = (this.mruCapacity - this.mutableMRU).coerceAtMost(amount)
        if (inserted <= 0) return 0

        val prev = this.mutableMRU
        this.mutableMRU += inserted
        this.onChange(prev)

        return inserted
    }

    override fun save(output: ValueOutput) {
        output.putInt("mru", this.mutableMRU)
        balance.save(output.child("balance"))
    }

    override fun load(input: ValueInput) {
        this.mutableMRU = input.getIntOr("mru", 0).coerceIn(0, this.mruCapacity)
        input.child("balance").getOrNull()?.let { this.balance.load(it) }
    }
}
