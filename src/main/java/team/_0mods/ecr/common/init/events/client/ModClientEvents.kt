@file:Mod.EventBusSubscriber(Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)

package team._0mods.ecr.common.init.events.client

import net.minecraft.client.gui.screens.MenuScreens
import net.minecraft.client.renderer.texture.TextureAtlas
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.event.EntityRenderersEvent
import net.minecraftforge.client.event.RegisterKeyMappingsEvent
import net.minecraftforge.client.event.RegisterParticleProvidersEvent
import net.minecraftforge.client.event.TextureStitchEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import team._0mods.ecr.LOGGER
import team._0mods.ecr.client.keys.kbList
import team._0mods.ecr.client.particle.ECParticleFactory
import team._0mods.ecr.client.renderer.MithrilineFurnaceRenderer
import team._0mods.ecr.client.screen.container.EnvoyerScreen
import team._0mods.ecr.client.screen.container.MatrixDestructorScreen
import team._0mods.ecr.client.screen.container.MithrilineFurnaceScreen
import team._0mods.ecr.common.init.registry.ECRegistry

@SubscribeEvent
fun onClientStartup(e: FMLClientSetupEvent) {
    LOGGER.info("Initializing client")
    e.enqueueWork {
        LOGGER.info("Registering screens")
        MenuScreens.register(ECRegistry.mithrilineFurnaceContainer.get(), ::MithrilineFurnaceScreen)
        MenuScreens.register(ECRegistry.matrixDestructorContainer.get(), ::MatrixDestructorScreen)
        MenuScreens.register(ECRegistry.envoyerContainer.get(), ::EnvoyerScreen)
    }
}

@SubscribeEvent
fun onRenderRegister(e: EntityRenderersEvent.RegisterRenderers) {
    e.registerBlockEntityRenderer(ECRegistry.mithrilineFurnaceEntity.get(), ::MithrilineFurnaceRenderer)
}

@SubscribeEvent
fun onLayerRegister(e: EntityRenderersEvent.RegisterLayerDefinitions) {
    e.registerLayerDefinition(MithrilineFurnaceRenderer.MF_LAYER, MithrilineFurnaceRenderer::createBodyLayer)
}

@SubscribeEvent
fun onParticleRegister(e: RegisterParticleProvidersEvent) {
    e.register(ECRegistry.ecParticle.get(), ::ECParticleFactory)
}

@SubscribeEvent
fun onTexturesSwitch(e: TextureStitchEvent.Pre) {
    if (!e.atlas.location().equals(TextureAtlas.LOCATION_BLOCKS)) return

    e.addSprite(MithrilineFurnaceRenderer.MF_RESOURCE_LOCATION.texture())
}

@SubscribeEvent
fun onKeybindRegister(e: RegisterKeyMappingsEvent) {
    kbList.forEach(e::register)
}
