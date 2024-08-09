package team._0mods.ecr.api.utils

import net.minecraft.world.item.ItemStack

object StackHelper {
    @JvmStatic
    fun areItemsEqual(stack1: ItemStack, stack2: ItemStack): Boolean {
        if (stack1.isEmpty && stack2.isEmpty) return true
        return !stack1.isEmpty && ItemStack.isSame(stack1, stack2)
    }

    @JvmStatic
    fun areStacksEqual(stack1: ItemStack, stack2: ItemStack): Boolean {
        return areItemsEqual(stack1, stack2) && ItemStack.isSameItemSameTags(stack1, stack2)
    }

    @JvmStatic
    fun canCombineStacks(stack1: ItemStack, stack2: ItemStack): Boolean {
        if (!stack1.isEmpty && stack2.isEmpty) return true
        return areStacksEqual(stack1, stack2) && (stack1.count + stack2.count) <= stack1.maxStackSize
    }
}