package team._0mods.ecr.api.item

import net.minecraft.world.item.ItemStack

interface HasSubItem {
    fun addSubItems(original: ItemStack): List<ItemStack>
}