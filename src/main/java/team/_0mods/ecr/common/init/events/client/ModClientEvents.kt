@file:Mod.EventBusSubscriber(Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)

package team._0mods.ecr.common.init.events.client

import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.event.EntityRenderersEvent
import net.minecraftforge.client.event.RegisterParticleProvidersEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import team._0mods.ecr.client.particle.ECParticleFactory
import team._0mods.ecr.client.renderer.MatrixDestructorRenderer
import team._0mods.ecr.client.renderer.MithrilineFurnaceRenderer
import team._0mods.ecr.common.init.registry.ECRegistry

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
