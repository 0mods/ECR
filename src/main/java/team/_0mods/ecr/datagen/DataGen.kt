package team._0mods.ecr.datagen

import net.minecraftforge.data.event.GatherDataEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import team._0mods.ecr.api.LOGGER
import team._0mods.ecr.api.ModId

@Mod.EventBusSubscriber(modid = ModId, bus = Mod.EventBusSubscriber.Bus.MOD)
object DataGen {
    @JvmStatic
    @SubscribeEvent
    fun onDataGeneration(e: GatherDataEvent) {
        val gen = e.generator
        val fileHelper = e.existingFileHelper

        LOGGER.info("Loaded datagen")

        gen.addProvider(e.includeServer(), ECRecipeProvider(gen))
    }
}
