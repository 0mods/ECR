 @file:Mod.EventBusSubscriber(Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)

package team._0mods.ecr.common.init.events.client

import net.minecraft.client.gui.screens.MenuScreens
import net.minecraft.client.renderer.ItemBlockRenderTypes
import net.minecraft.client.renderer.RenderType
import net.minecraft.world.level.block.Block
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
import team._0mods.ecr.client.renderer.*
import team._0mods.ecr.client.screen.menu.*
import team._0mods.ecr.common.init.registry.ECRegistry
import ru.hollowhorizon.hc.common.events.SubscribeEvent as HCSubscribe

private val cutoutTextures by lazy {
    listOf<Block>(
        ECRegistry.airCluster.get(),
        ECRegistry.earthCluster.get(),
        ECRegistry.waterCluster.get(),
        ECRegistry.flameCluster.get()
    )
}

private val invisibleTextures by lazy {
    listOf<Block>(
        ECRegistry.magicTable.get()
    )
}

@SubscribeEvent
fun onClientStartup(e: FMLClientSetupEvent) {
    LOGGER.info("Initializing client")
    @Suppress("DEPRECATION")
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

        cutoutTextures.forEach { ItemBlockRenderTypes.setRenderLayer(it, RenderType.cutout()) }
        invisibleTextures.forEach { ItemBlockRenderTypes.setRenderLayer(it, RenderType.translucent()) }
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
