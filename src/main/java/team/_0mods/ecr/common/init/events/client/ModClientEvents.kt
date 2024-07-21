package team._0mods.ecr.common.init.events.client

import net.minecraft.client.renderer.item.ItemProperties
import net.minecraft.resources.ResourceLocation
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import team._0mods.ecr.LOGGER
import team._0mods.ecr.ModId
import team._0mods.ecr.common.init.registry.ECRegistry
import team._0mods.ecr.common.items.ECBook
import team._0mods.ecr.common.items.ECBook.Type

class ModClientEvents {
    @SubscribeEvent
    fun onClientStartup(e: FMLClientSetupEvent) {
        LOGGER.info("Initializing client")
    }
}
