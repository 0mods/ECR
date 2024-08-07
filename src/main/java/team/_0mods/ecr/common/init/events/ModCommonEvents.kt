@file:Mod.EventBusSubscriber(modid = ModId, bus = Mod.EventBusSubscriber.Bus.MOD)

package team._0mods.ecr.common.init.events

import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import team._0mods.ecr.ModId

@SubscribeEvent
fun onModSetup(e: FMLCommonSetupEvent) {}

@SubscribeEvent
fun onCapabilityRegister(e: RegisterCapabilitiesEvent) {

}