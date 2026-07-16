package com.algorithmlx.ecr.api.item

import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import org.jetbrains.annotations.ApiStatus

interface BoundGem {
    val world: String? get() = null

    val dimensionalBounds: Boolean get() = true

    // use a NOT reversed list! I.e., if you are uses 1000, 100, .., 1, the system will count 1, .., 100, 1000, which is wrong!
    val transferStrength: Array<Int> get() = arrayOf(1, 10, 50, 100, 1000)

    // TODO it is not work.
    @get:ApiStatus.Experimental
    @get:ApiStatus.NonExtendable
    val boundRadius: Double get() = 16.0

    fun getBoundPos(stack: ItemStack): BlockPos?

    fun setBoundPos(stack: ItemStack, blockPos: BlockPos?)

    fun getWorld(stack: ItemStack): ResourceKey<Level>?

    fun setWorld(stack: ItemStack, world: ResourceKey<Level>?)
}
