package team._0mods.ecr.common.init.events

import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import team._0mods.ecr.ModId

@Mod.EventBusSubscriber(modid = ModId, bus = Mod.EventBusSubscriber.Bus.MOD)
class ModCommonEvents {
    @SubscribeEvent
    fun onModSetup(e: FMLCommonSetupEvent) {}
}
