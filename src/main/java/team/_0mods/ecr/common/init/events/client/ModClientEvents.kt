@file:Mod.EventBusSubscriber(Dist.CLIENT, modid = ModId, bus = Mod.EventBusSubscriber.Bus.MOD)

package team._0mods.ecr.common.init.events.client

import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import team._0mods.ecr.LOGGER
import team._0mods.ecr.ModId

@SubscribeEvent
fun onClientStartup(e: FMLClientSetupEvent) {
    LOGGER.info("Initializing client")
}
