@file:Mod.EventBusSubscriber(modid = ModId, bus = Mod.EventBusSubscriber.Bus.MOD)

package team._0mods.ecr.common.init.events

import net.minecraft.world.entity.player.Player
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent
import net.minecraftforge.event.AttachCapabilitiesEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import team._0mods.ecr.ModId
import team._0mods.ecr.api.rl
import team._0mods.ecr.common.capability.impl.PlayerMRUImpl
import team._0mods.ecr.common.init.registry.ECCapabilities

@SubscribeEvent
fun onModSetup(e: FMLCommonSetupEvent) {}

@SubscribeEvent
fun onCapabilityRegister(e: RegisterCapabilitiesEvent) {

}

fun onCapabilityPlayerAttach(e: AttachCapabilitiesEvent<Player>) {
    if (!e.`object`.getCapability(ECCapabilities.PLAYER_MRU).isPresent) e.addCapability("$ModId:player_mru".rl, PlayerMRUImpl.Provider())
}