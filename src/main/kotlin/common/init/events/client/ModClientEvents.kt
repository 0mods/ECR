//? if forge {
/*@file:Mod.EventBusSubscriber(Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
package team._0mods.ecr.common.init.events.client

import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import team._0mods.ecr.initClient

@SubscribeEvent
fun onModLoad(e: FMLClientSetupEvent) {
    e.enqueueWork { initClient() }
}
*///?}
