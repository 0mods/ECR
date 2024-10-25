@file:Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)

package team._0mods.ecr.common.init.events

import kotlinx.serialization.ExperimentalSerializationApi
import net.minecraft.world.item.BlockItem
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import net.minecraftforge.registries.ForgeRegistries
import team._0mods.ecr.api.ModId
import team._0mods.ecr.api.utils.loadConfig
import team._0mods.ecr.common.api.NoTab
import team._0mods.ecr.common.init.config.ECCommonConfig
import team._0mods.ecr.common.init.registry.ECRegistry

@OptIn(ExperimentalSerializationApi::class)
@SubscribeEvent
fun onModSetup(e: FMLCommonSetupEvent) {
    ECCommonConfig.instance = ECCommonConfig().loadConfig("essential-craft/common")
}

@SubscribeEvent
fun onBuildCreativeTabs(e: BuildCreativeModeTabContentsEvent) {
    ForgeRegistries.ITEMS.filter { ForgeRegistries.ITEMS.getKey(it)!!.namespace.contains(ModId) }.forEach {
        if (it is NoTab) return

        if (it !is BlockItem) {
            if (e.tab == ECRegistry.tabItems.get()) e.accept(it)
        } else {
            if (e.tab == ECRegistry.tabBlocks.get()) e.accept(it)
        }
    }
}
