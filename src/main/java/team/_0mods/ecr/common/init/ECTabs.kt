package team._0mods.ecr.common.init

import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack
import team._0mods.ecr.ModId
import team._0mods.ecr.common.items.ECBook

object ECTabs {
    val tabItems = object : CreativeModeTab("$ModId.items") {
        override fun makeIcon(): ItemStack = ItemStack(ECBook.bookList[3])
    }
}