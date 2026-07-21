package com.algorithmlx.ecr.neoforge.init

import com.algorithmlx.ecr.api.client.render.MultiblockPreviewGuiBridge
import com.algorithmlx.ecr.api.client.render.MultiblockPreviewPictureRenderer
import com.algorithmlx.ecr.api.client.render.MultiblockPreviewRenderState
import com.algorithmlx.ecr.api.research.*
import com.algorithmlx.ecr.client.book.ResearchBookClient
import com.algorithmlx.ecr.client.renderer.MatrixDestructorRenderer
import com.algorithmlx.ecr.client.renderer.MithrilineFurnaceRenderer
import com.algorithmlx.ecr.client.screen.MagicTableMenuScreen
import com.algorithmlx.ecr.client.screen.MatrixDestructorScreen
import com.algorithmlx.ecr.client.screen.MithrilineFurnaceScreen
import com.algorithmlx.ecr.registry.BlockEntityTypeRegistry
import com.algorithmlx.ecr.registry.MenuTypeRegistry
import com.algorithmlx.ecr.network.BoundGemTooltipNetwork
import com.algorithmlx.ecr.network.BoundGemTooltipResponsePayload
import com.algorithmlx.ecr.network.FinishCraftParticle
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers
import net.minecraft.core.particles.ParticleTypes
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import net.neoforged.neoforge.client.event.EntityRenderersEvent
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent
import net.neoforged.neoforge.client.event.RegisterPictureInPictureRenderersEvent
import net.neoforged.neoforge.client.network.ClientPacketDistributor
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent
import kotlin.random.Random

object NeoForgeClientInit {
    fun init(bus: IEventBus) {
        MultiblockPreviewGuiBridge.install(GuiGraphicsExtractor::submitPictureInPictureRenderState)
        bus.addListener(::onRegisterPIPRenders)

        bus.addListener(::onRegisterClientPayloads)
        bus.addListener(::onClientInit)
        bus.addListener(::onMenuScreen)

        bus.addListener(::onRegisterEntityModelLayer)
        bus.addListener(::onRegisterEntityRenderers)
    }

    private fun onClientInit(event: FMLClientSetupEvent) {
        event.enqueueWork {
            ResearchBookClient.init()

            ResearchNetwork.completeResearch = { ClientPacketDistributor.sendToServer(CompleteResearchPayload(it)) }
            ResearchNetwork.updateFavorite = { research, spread, color -> ClientPacketDistributor.sendToServer(FavoriteResearchPayload(research, spread, color)) }
            ResearchNetwork.updateView = { state -> runCatching { ClientPacketDistributor.sendToServer(UpdateBookViewPayload(state)) } }
            BoundGemTooltipNetwork.currentDimension = { Minecraft.getInstance().level?.dimension() }
            BoundGemTooltipNetwork.sendRequestToServer = { payload -> runCatching { ClientPacketDistributor.sendToServer(payload) } }

            BlockEntityRenderers.register(BlockEntityTypeRegistry.mithrilineFurnace, ::MithrilineFurnaceRenderer)
            BlockEntityRenderers.register(BlockEntityTypeRegistry.matrixDestructor, ::MatrixDestructorRenderer)
        }
    }

    private fun onMenuScreen(event: RegisterMenuScreensEvent) {
        event.register(MenuTypeRegistry.mithrilineFurnace, ::MithrilineFurnaceScreen)
        event.register(MenuTypeRegistry.magicTable, ::MagicTableMenuScreen)
        event.register(MenuTypeRegistry.matrixDestructor, ::MatrixDestructorScreen)
    }

    private fun onRegisterClientPayloads(event: RegisterClientPayloadHandlersEvent) {
        event.register(ResearchSyncPayload.TYPE) { payload, _ -> ClientResearchState.apply(payload) }
        event.register(ResearchProgressPayload.TYPE) { payload, _ -> ClientResearchState.apply(payload) }
        event.register(BoundGemTooltipResponsePayload.TYPE) { payload, _ -> BoundGemTooltipNetwork.acceptResponse(payload) }
        event.register(FinishCraftParticle.TYPE) { payload, _ ->
            val level = Minecraft.getInstance().level ?: return@register
            (0 ..< payload.count).forEach { _ ->
                level.addParticle(
                    ParticleTypes.POOF, payload.x, payload.y + Random.nextDouble(0.15, 0.6), payload.z,
                    Random.nextDouble(-0.06, 0.06), Random.nextDouble(0.0, 0.15),
                    Random.nextDouble(-0.06, 0.06)
                )
            }
        }
    }

    private fun onRegisterPIPRenders(event: RegisterPictureInPictureRenderersEvent) {
        event.register(MultiblockPreviewRenderState::class.java, ::MultiblockPreviewPictureRenderer)
    }

    private fun onRegisterEntityRenderers(event: EntityRenderersEvent.RegisterRenderers) {
        event.registerBlockEntityRenderer(BlockEntityTypeRegistry.mithrilineFurnace, ::MithrilineFurnaceRenderer)
    }

    private fun onRegisterEntityModelLayer(event: EntityRenderersEvent.RegisterLayerDefinitions) {
        event.registerLayerDefinition(MithrilineFurnaceRenderer.MF_LAYER, MithrilineFurnaceRenderer::createBodyLayer)
    }
}
