package com.algorithmlx.ecr.api.mru.balance

import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput

interface MRUBalance {
    val upperBalance: Double
    val lowerBalance: Double
}

interface MutableMRUBalance : MRUBalance {
    val maxUpperBalance: Double

    val maxLowerBalance: Double

    val minUpperBalance: Double

    val minLowerBalance: Double

    fun setUpperBalance(newBalance: Double)

    fun setLowerBalance(newBalance: Double)

    fun updateUpperBalance(changeOn: Double): Double

    fun updateLowerBalance(changeOn: Double): Double

    fun toImmutable(): MRUBalance

    fun save(output: ValueOutput)

    fun load(input: ValueInput)
}
