@file:Mod.EventBusSubscriber(Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)

package team._0mods.ecr.common.init.events.client

import net.minecraft.client.gui.screens.MenuScreens
import net.minecraft.client.renderer.texture.TextureAtlas
import net.minecraft.server.packs.resources.Resource
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.event.EntityRenderersEvent
import net.minecraftforge.client.event.RegisterKeyMappingsEvent
import net.minecraftforge.client.event.RegisterParticleProvidersEvent
import net.minecraftforge.client.event.TextureStitchEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import ru.hollowhorizon.hc.client.utils.HollowPack
import team._0mods.ecr.LOGGER
import team._0mods.ecr.api.registries.ECRegistries
import team._0mods.ecr.api.utils.ecRL
import team._0mods.ecr.client.keys.kbList
import team._0mods.ecr.client.particle.ECParticleFactory
import team._0mods.ecr.client.renderer.MithrilineFurnaceRenderer
import team._0mods.ecr.client.screen.container.EnvoyerScreen
import team._0mods.ecr.client.screen.container.MatrixDestructorScreen
import team._0mods.ecr.client.screen.container.MithrilineFurnaceScreen
import team._0mods.ecr.common.init.registry.ECRegistry
import team._0mods.ecr.mixin.accessors.HollowPackAccessor
import java.io.ByteArrayInputStream
import java.io.InputStream
import kotlin.collections.set

@SubscribeEvent
fun onClientStartup(e: FMLClientSetupEvent) {
    LOGGER.info("Initializing client")
    makeContent()
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

private fun makeContent() {
    val entries = ECRegistries.BOOK_TYPES.registries.keys
    // textures for main book
    val sb = buildString {
        append("{").append('\n')
        //"parent":"item/generated",
        append("\"parent\":\"item/generated\",")
        //"textures":{"layer0":"ecreimagined:item/basic_book"},
        append("\"textures\":{\"layer0\":\"ecreimagined:item/basic_book\"},")
        //"overrides":[
        append("\"overrides\":[")

        for (i in 0 ..< entries.size) {
            val id = entries.toList()[i]
            append("{\"predicate\":{\"ecreimagined:type\":$i.0},\"model\":\"${id.namespace}:item/${id.path}\"}")

            if (i < entries.size - 1) append(',')
        }

        append("]}")
    }

    (HollowPack as HollowPackAccessor).resourceMap()["research_book".ecRL] =
        Resource.IoSupplier<InputStream> { ByteArrayInputStream(sb.toString().toByteArray()) }

    // textures for types
    for (i in 0 ..< entries.size) {
        val id = entries.toList()[i]
        val sb = buildString {
            append("{").append('\n')
            append("\"parent\":\"item/generated\",")
            append("\"textures\":{\"layer0\":\"${id.namespace}:item/${id.path}\"}")
            append("]}")
        }

        (HollowPack as HollowPackAccessor).resourceMap()[id] =
            Resource.IoSupplier<InputStream> { ByteArrayInputStream(sb.toString().toByteArray()) }
    }

    (HollowPack as HollowPackAccessor).resourceMap().forEach {
       LOGGER.debug("Id: {}, content: {}", it.key.toString(), it.value.toString())
    }
}