@file:Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)

package team._0mods.ecr.common.init.events

import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.ItemLike
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.registries.ForgeRegistries
import team._0mods.ecr.api.ModId
import team._0mods.ecr.api.item.HasSubItem
import team._0mods.ecr.common.api.NoTab
import team._0mods.ecr.common.init.registry.ECRegistry

@SubscribeEvent
fun onBuildCreativeTabs(e: BuildCreativeModeTabContentsEvent) {
    ForgeRegistries.ITEMS.filter { ForgeRegistries.ITEMS.getKey(it)!!.namespace.contains(ModId) }.forEach { it ->
        if (it is NoTab) return@forEach

        if (it is HasSubItem) {
            it.addSubItems(ItemStack(it)).forEach { stack -> e.accept(ECRegistry.tabItems.get(), stack) }
            return@forEach
        }

        e.accept(if (it is BlockItem) ECRegistry.tabBlocks.get() else ECRegistry.tabItems.get(), it)
    }
}

private fun BuildCreativeModeTabContentsEvent.accept(tab: CreativeModeTab, stack: ItemStack) {
    val t = this.tab
    if (tab == t) {
        this.accept(stack)
    }
}

private fun BuildCreativeModeTabContentsEvent.accept(tab: CreativeModeTab, item: ItemLike) {
    this.accept(tab, ItemStack(item))
}