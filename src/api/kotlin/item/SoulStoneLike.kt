package team._0mods.ecr.api.item

import net.minecraft.world.item.ItemStack
import team._0mods.ecr.api.utils.SoulStoneUtils.isCreative

interface SoulStoneLike: HasSubItem {
    val receiveCount: Int

    val extractCount: Int

    override fun addSubItems(original: ItemStack): List<ItemStack> = listOf(original, original.copy().apply { this.isCreative = true })
}