package com.algorithmlx.ecr.common.components

import com.algorithmlx.ecr.api.mru.MRUStorage
import com.algorithmlx.ecr.api.mru.MRUType
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput
import kotlin.math.max
import kotlin.math.min

data class MRUStorageComponent(
    override val mruCapacity: Int,
    override val mruType: MRUType,
    val onChange: (Int) -> Unit = {}
): MRUStorage {
    private var mru0 = 0

    override val mru: Int = mru0

    fun set(amount: Int) {
        if (amount < 0) return
        if (this.mru == amount) return

        val prev = this.mru
        this.mru0 = amount
        this.onChange(prev)
    }

    fun extract(amount: Int): Int {
        if (amount < 0) return 0
        val extracted = this.mru.coerceAtMost(amount)
        if (extracted > 0) {
            val prev = this.mru
            this.mru0 -= extracted
            onChange(prev)
            return extracted
        }

        return 0
    }

    fun insert(amount: Int): Int {
        if (amount < 0) return 0

        val inserted = min(this.mruCapacity - this.mru, amount)
        if (inserted > 0) {
            val prev = this.mru
            this.mru0 += inserted
            onChange(prev)
            return inserted
        }

        return 0
    }

    fun save(output: ValueOutput) {
        output.putInt("mru", this.mru)
    }

    fun load(input: ValueInput) {
        this.mru0 = max(0, input.getIntOr("mru", 0))
    }
}
