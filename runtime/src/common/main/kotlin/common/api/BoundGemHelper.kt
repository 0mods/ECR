package com.algorithmlx.ecr.common.api

import com.algorithmlx.ecr.common.components.BoundGemComponent
import com.algorithmlx.ecr.registry.DataComponentRegistry
import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import java.util.Optional
import kotlin.jvm.optionals.getOrNull

object BoundGemHelper {
    @JvmStatic
    fun getBoundPos(stack: ItemStack): BlockPos? = stack.get(DataComponentRegistry.boundGem)?.pos

    @JvmStatic
    fun setBoundPos(stack: ItemStack, pos: BlockPos?) {
        val data = stack.get(DataComponentRegistry.boundGem)
        if (pos != null) stack.set(
            DataComponentRegistry.boundGem,
            data?.copy(pos = pos) ?: BoundGemComponent(pos)
        ) else stack.set(DataComponentRegistry.boundGem, null)
    }

    @JvmStatic
    fun setWorld(stack: ItemStack, world: ResourceKey<Level>?) {
        val data = stack.get(DataComponentRegistry.boundGem) ?: return
        stack.set(DataComponentRegistry.boundGem, data.copy(dimension = Optional.ofNullable(world)))
    }

    @JvmStatic
    fun getWorld(stack: ItemStack): ResourceKey<Level>? = stack.get(DataComponentRegistry.boundGem)
        ?.dimension
        ?.getOrNull()
}
