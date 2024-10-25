@file:Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)

package team._0mods.ecr.common.init.events

import kotlinx.serialization.ExperimentalSerializationApi
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import net.minecraftforge.registries.ForgeRegistries
import team._0mods.ecr.api.utils.loadConfig
import team._0mods.ecr.common.api.CustomTab
import team._0mods.ecr.common.init.config.ECCommonConfig

@OptIn(ExperimentalSerializationApi::class)
@SubscribeEvent
fun onModSetup(e: FMLCommonSetupEvent) {
    ECCommonConfig.instance = ECCommonConfig().loadConfig("essential-craft/common")
}

@SubscribeEvent
fun onBuildCreativeTabs(e: BuildCreativeModeTabContentsEvent) {
    ForgeRegistries.ITEMS.filter { it is CustomTab }.forEach {
        it as CustomTab
        if (it.tab == e.tab) {
            e.accept(it)
        }
    }
}
