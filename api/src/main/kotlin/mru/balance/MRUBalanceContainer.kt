package com.algorithmlx.ecr.api.mru.balance

import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput

data class MRUBalanceContainer(
    override val maxUpperBalance: Double,
    override val maxLowerBalance: Double,
    override val minUpperBalance: Double,
    override val minLowerBalance: Double,
) : MutableMRUBalance {
    private var mutableUpperBalance = 0.0
    private var mutableLowerBalance = 0.0

    override val upperBalance: Double get() = mutableUpperBalance
    override val lowerBalance: Double get() = mutableLowerBalance

    override fun setUpperBalance(newBalance: Double) {
        this.mutableUpperBalance = newBalance.coerceIn(minUpperBalance, maxUpperBalance)
    }

    override fun setLowerBalance(newBalance: Double) {
        this.mutableLowerBalance = newBalance.coerceIn(minLowerBalance, maxLowerBalance)
    }

    override fun updateUpperBalance(changeOn: Double): Double {
        val oldBalance = mutableUpperBalance
        val newBalance = (oldBalance + changeOn).coerceIn(minUpperBalance, maxUpperBalance)

        mutableUpperBalance = newBalance
        return newBalance - oldBalance
    }

    override fun updateLowerBalance(changeOn: Double): Double {
        val oldBalance = mutableLowerBalance
        val newBalance = (oldBalance + changeOn).coerceIn(minLowerBalance, maxLowerBalance)

        mutableLowerBalance = newBalance
        return newBalance - oldBalance
    }

    override fun toImmutable(): MRUBalance = ImmutableMRUBalance(this.upperBalance, this.lowerBalance)

    override fun save(output: ValueOutput) {
        output.putDouble("upper_balance", this.mutableUpperBalance)
        output.putDouble("lower_balance", this.mutableLowerBalance)
    }

    override fun load(input: ValueInput) {
        this.setUpperBalance(input.getDoubleOr("upper_balance", 0.0))
        this.setLowerBalance(input.getDoubleOr("lower_balance", 0.0))
    }
}

data class ImmutableMRUBalance(
    override val upperBalance: Double,
    override val lowerBalance: Double,
) : MRUBalance
