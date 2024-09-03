package team._0mods.ecr.common.init.registry

import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack
import team._0mods.ecr.ModId

object ECTabs {
    val tabItems = object : CreativeModeTab("$ModId.items") {
        override fun makeIcon(): ItemStack = ItemStack(ECRegistry.elementalGem.get())
    }

    val tabBlocks = object : CreativeModeTab("$ModId.blocks") {
        override fun makeIcon(): ItemStack = ItemStack(ECRegistry.mithrilineFurnace.get())
    }
}
