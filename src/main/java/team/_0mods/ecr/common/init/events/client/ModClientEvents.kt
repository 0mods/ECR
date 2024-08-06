@file:Mod.EventBusSubscriber(Dist.CLIENT, modid = ModId, bus = Mod.EventBusSubscriber.Bus.MOD)

package team._0mods.ecr.common.init.events.client

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.MenuScreens
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.event.EntityRenderersEvent
import net.minecraftforge.client.event.RegisterParticleProvidersEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import team._0mods.ecr.LOGGER
import team._0mods.ecr.ModId
import team._0mods.ecr.client.particle.ECParticle
import team._0mods.ecr.client.particle.ECParticleFactory
import team._0mods.ecr.client.renderer.MithrilineFurnaceRenderer
import team._0mods.ecr.client.screen.container.MithrilineFurnaceScreen
import team._0mods.ecr.common.init.registry.ECRegistry

@SubscribeEvent
fun onClientStartup(e: FMLClientSetupEvent) {
    LOGGER.info("Initializing client")
    e.enqueueWork {
        LOGGER.info("Registering screens")
        MenuScreens.register(ECRegistry.mithrilineFurnaceContainer.get(), ::MithrilineFurnaceScreen)
    }
}

@SubscribeEvent
fun onRenderRegister(e: EntityRenderersEvent.RegisterRenderers) {
    e.registerBlockEntityRenderer(ECRegistry.mithrilineFurnace.second, ::MithrilineFurnaceRenderer)
}

@SubscribeEvent
fun onLayerRegister(e: EntityRenderersEvent.RegisterLayerDefinitions) {
    e.registerLayerDefinition(MithrilineFurnaceRenderer.MF_LAYER, MithrilineFurnaceRenderer::createBodyLayer)
}

@SubscribeEvent
fun onParticleRegister(e: RegisterParticleProvidersEvent) {
    e.register(ECRegistry.ecParticle.get(), ::ECParticleFactory)
}
