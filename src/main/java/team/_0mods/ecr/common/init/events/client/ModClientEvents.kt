 @file:Mod.EventBusSubscriber(Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)

package team._0mods.ecr.common.init.events.client

import net.minecraft.client.gui.screens.MenuScreens
import net.minecraft.client.renderer.ItemBlockRenderTypes
import net.minecraft.client.renderer.RenderType
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.event.EntityRenderersEvent
import net.minecraftforge.client.event.RegisterParticleProvidersEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import ru.hollowhorizon.hc.common.events.registry.RegisterKeyBindingsEvent
import team._0mods.ecr.api.LOGGER
import team._0mods.ecr.client.keys.ECKeys
import team._0mods.ecr.client.particle.ECParticleFactory
import team._0mods.ecr.client.renderer.MatrixDestructorRenderer
import team._0mods.ecr.client.renderer.MithrilineFurnaceRenderer
import team._0mods.ecr.client.screen.container.MatrixDestructorScreen
import team._0mods.ecr.client.screen.container.MithrilineFurnaceScreen
import team._0mods.ecr.client.screen.container.XLikeScreen
import team._0mods.ecr.common.init.registry.ECRegistry
import ru.hollowhorizon.hc.common.events.SubscribeEvent as HCSubscribe

 @SubscribeEvent
fun onClientStartup(e: FMLClientSetupEvent) {
    LOGGER.info("Initializing client")
    @Suppress("REMOVAL", "DEPRECATION")
    e.enqueueWork {
        LOGGER.info("Registering screens")
        MenuScreens.register(ECRegistry.mithrilineFurnaceMenu.get(), ::MithrilineFurnaceScreen)
        MenuScreens.register(ECRegistry.matrixDestructorMenu.get(), ::MatrixDestructorScreen)
        MenuScreens.register(ECRegistry.envoyerMenu.get()) { menu, inv, title ->
            XLikeScreen.Envoyer(menu, inv, title)
        }
        MenuScreens.register(ECRegistry.magicTableMenu.get()) { menu, inv, title ->
            XLikeScreen.MagicTable(menu, inv, title)
        }

        ItemBlockRenderTypes.setRenderLayer(ECRegistry.airCluster.get(), RenderType.cutout())
        ItemBlockRenderTypes.setRenderLayer(ECRegistry.earthCluster.get(), RenderType.cutout())
        ItemBlockRenderTypes.setRenderLayer(ECRegistry.waterCluster.get(), RenderType.cutout())
        ItemBlockRenderTypes.setRenderLayer(ECRegistry.flameCluster.get(), RenderType.cutout())
    }
}

@SubscribeEvent
fun onRenderRegister(e: EntityRenderersEvent.RegisterRenderers) {
    e.registerBlockEntityRenderer(ECRegistry.mithrilineFurnaceEntity.get(), ::MithrilineFurnaceRenderer)
    e.registerBlockEntityRenderer(ECRegistry.matrixDestructorEntity.get(), ::MatrixDestructorRenderer)
}

@SubscribeEvent
fun onLayerRegister(e: EntityRenderersEvent.RegisterLayerDefinitions) {
    e.registerLayerDefinition(MithrilineFurnaceRenderer.MF_LAYER, MithrilineFurnaceRenderer::createBodyLayer)
}

@SubscribeEvent
fun onParticleRegister(e: RegisterParticleProvidersEvent) {
    e.registerSpriteSet(ECRegistry.ecParticle.get(), ::ECParticleFactory)
}

@HCSubscribe
fun onKeyBindRegister(e: RegisterKeyBindingsEvent) {
    ECKeys.kbList.forEach(e::registerKeyMapping)
}
