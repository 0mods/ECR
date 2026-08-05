package com.algorithmlx.ecr.neoforge.init

import com.algorithmlx.ecr.api.client.render.MultiblockPreviewGuiBridge
import com.algorithmlx.ecr.api.client.render.MultiblockPreviewPictureRenderer
import com.algorithmlx.ecr.api.client.render.MultiblockPreviewRenderState
import com.algorithmlx.ecr.api.particle.BedrockParticleRenderTypes
import com.algorithmlx.ecr.api.geo.GeoAnimationNetwork
import com.algorithmlx.ecr.api.geo.GeoBlockAnimationPayload
import com.algorithmlx.ecr.api.geo.GeoBlockAnimationStopPayload
import com.algorithmlx.ecr.api.geo.GeoEntityAnimationPayload
import com.algorithmlx.ecr.api.geo.GeoEntityAnimationStopPayload
import com.algorithmlx.ecr.api.geo.GeoItemAnimationPayload
import com.algorithmlx.ecr.api.geo.GeoItemAnimationStopPayload
import com.algorithmlx.ecr.api.geo.client.BedrockGeoAssets
import com.algorithmlx.ecr.api.geo.client.BedrockGeoItemRenderer
import com.algorithmlx.ecr.api.geo.client.ClientGeoAnimations
import com.algorithmlx.ecr.api.particle.BedrockParticles
import com.algorithmlx.ecr.api.particle.ClientParticleSystems
import com.algorithmlx.ecr.api.research.*
import com.algorithmlx.ecr.api.utils.ecRL
import com.algorithmlx.ecr.client.book.ResearchBookClient
import com.algorithmlx.ecr.client.renderer.BoundGemLinkRenderer
import com.algorithmlx.ecr.client.renderer.EnrichmentChamberControllerRenderer
import com.algorithmlx.ecr.client.renderer.AssembledMultiblockRenderer
import com.algorithmlx.ecr.client.renderer.MatrixDestructorRenderer
import com.algorithmlx.ecr.client.renderer.MithrilineFurnaceRenderer
import com.algorithmlx.ecr.client.screen.MagicTableMenuScreen
import com.algorithmlx.ecr.client.screen.MatrixDestructorScreen
import com.algorithmlx.ecr.client.screen.MithrilineFurnaceScreen
import com.algorithmlx.ecr.client.screen.RayTowerScreen
import com.algorithmlx.ecr.common.block.entity.AssembledMultiblockPartBlockEntity
import com.algorithmlx.ecr.common.block.entity.RayTowerEntity
import com.algorithmlx.ecr.neoforge.client.NeoForgeConnectedTextures
import com.algorithmlx.ecr.neoforge.client.NeoForgeIrisCompatibility
import com.algorithmlx.ecr.client.ECRConnectedTextures
import com.algorithmlx.ecr.client.screen.EnrichmentChamberReceiverScreen
import com.algorithmlx.ecr.registry.BlockEntityTypeRegistry
import com.algorithmlx.ecr.registry.MenuTypeRegistry
import com.algorithmlx.ecr.network.BoundGemTooltipNetwork
import com.algorithmlx.ecr.network.BoundGemTooltipResponsePayload
import com.algorithmlx.ecr.network.FinishCraftParticle
import com.algorithmlx.ecr.network.SoulStoneTooltipNetwork
import com.algorithmlx.ecr.network.SoulStoneTooltipResponsePayload
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers
import net.minecraft.client.renderer.item.properties.select.ItemBlockState
import net.minecraft.client.renderer.rendertype.RenderTypes
import net.minecraft.core.particles.ParticleTypes
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import net.neoforged.neoforge.client.event.EntityRenderersEvent
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent
import net.neoforged.neoforge.client.event.ClientTickEvent
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent
import net.neoforged.neoforge.client.event.RegisterPictureInPictureRenderersEvent
import net.neoforged.neoforge.client.event.RegisterSpecialModelRendererEvent
import net.neoforged.neoforge.client.event.SubmitCustomGeometryEvent
import net.neoforged.neoforge.client.network.ClientPacketDistributor
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent
import net.neoforged.neoforge.common.NeoForge
import kotlin.random.Random

object NeoForgeClientInit {
    fun init(bus: IEventBus) {
        NeoForgeIrisCompatibility.init()
        NeoForgeConnectedTextures.init(bus)
        ECRConnectedTextures.init()
        BedrockParticleRenderTypes.init()
        MultiblockPreviewGuiBridge.install(GuiGraphicsExtractor::submitPictureInPictureRenderState)
        bus.addListener(::onRegisterPIPRenders)
        bus.addListener(::onRegisterClientReloadListeners)

        bus.addListener(::onRegisterClientPayloads)
        bus.addListener(::onRegisterSpecialModelRenderer)
        bus.addListener(::onClientInit)
        bus.addListener(::onMenuScreen)

        bus.addListener(::onRegisterEntityModelLayer)
        bus.addListener(::onRegisterEntityRenderers)

        NeoForge.EVENT_BUS.addListener(::onClientTick)
        NeoForge.EVENT_BUS.addListener(::onClientLogout)
        NeoForge.EVENT_BUS.addListener(::onSubmitCustomGeometry)
    }

    private fun onRegisterClientReloadListeners(event: AddClientReloadListenersEvent) {
        event.addListener("bedrock_particles".ecRL, BedrockParticles)
        event.addListener("bedrock_geo".ecRL, BedrockGeoAssets)
    }

    private fun onClientTick(event: ClientTickEvent.Post) {
        Minecraft.getInstance().level?.let { ClientParticleSystems.get(it)?.update() }
    }

    private fun onClientLogout(event: ClientPlayerNetworkEvent.LoggingOut) {
        SoulStoneTooltipNetwork.clear()
    }

    private fun onSubmitCustomGeometry(event: SubmitCustomGeometryEvent) {
        val minecraft = Minecraft.getInstance()
        val level = minecraft.level ?: return
        ClientParticleSystems.get(level)?.submit(
            event.poseStack,
            event.submitNodeCollector,
            event.levelRenderState,
            minecraft.deltaTracker.getGameTimeDeltaPartialTick(false),
            minecraft.player?.uuid,
            minecraft.options.cameraType.isFirstPerson,
        )
        BoundGemLinkRenderer.submit(
            event.poseStack,
            event.submitNodeCollector,
            event.levelRenderState
        )
    }

    private fun onRegisterSpecialModelRenderer(event: RegisterSpecialModelRendererEvent) {
        event.register(BedrockGeoItemRenderer.ID, BedrockGeoItemRenderer.Unbaked.CODEC)
    }

    private fun onClientInit(event: FMLClientSetupEvent) {
        event.enqueueWork {
            ResearchBookClient.init()

            ResearchNetwork.completeResearch = { ClientPacketDistributor.sendToServer(CompleteResearchPayload(it)) }
            ResearchNetwork.updateFavorite = { research, spread, color -> ClientPacketDistributor.sendToServer(FavoriteResearchPayload(research, spread, color)) }
            ResearchNetwork.updateView = { state -> runCatching { ClientPacketDistributor.sendToServer(UpdateBookViewPayload(state)) } }
            BoundGemTooltipNetwork.currentDimension = { Minecraft.getInstance().level?.dimension() }
            BoundGemTooltipNetwork.sendRequestToServer = { payload -> runCatching { ClientPacketDistributor.sendToServer(payload) } }
            SoulStoneTooltipNetwork.sendRequestToServer = { payload -> runCatching { ClientPacketDistributor.sendToServer(payload) } }
            GeoAnimationNetwork.playClientBlockAnimation = ClientGeoAnimations::handle
            GeoAnimationNetwork.playClientEntityAnimation = ClientGeoAnimations::handle
            GeoAnimationNetwork.playClientItemAnimation = ClientGeoAnimations::handle
            GeoAnimationNetwork.stopClientBlockAnimation = ClientGeoAnimations::handle
            GeoAnimationNetwork.stopClientEntityAnimation = ClientGeoAnimations::handle
            GeoAnimationNetwork.stopClientItemAnimation = ClientGeoAnimations::handle

            BlockEntityRenderers.register(BlockEntityTypeRegistry.instance.mithrilineFurnace, ::MithrilineFurnaceRenderer)
            BlockEntityRenderers.register(
                BlockEntityTypeRegistry.instance.assembledMultiblockPart,
                { context -> AssembledMultiblockRenderer<AssembledMultiblockPartBlockEntity>(context) }
            )
            BlockEntityRenderers.register(
                BlockEntityTypeRegistry.instance.rayTower,
                { context -> AssembledMultiblockRenderer<RayTowerEntity>(context) }
            )
            BlockEntityRenderers.register(BlockEntityTypeRegistry.instance.matrixDestructor, ::MatrixDestructorRenderer)
            BlockEntityRenderers.register(
                BlockEntityTypeRegistry.instance.enrichmentChamberController,
                ::EnrichmentChamberControllerRenderer
            )
        }
    }

    private fun onMenuScreen(event: RegisterMenuScreensEvent) {
        event.register(MenuTypeRegistry.instance.mithrilineFurnace, ::MithrilineFurnaceScreen)
        event.register(MenuTypeRegistry.instance.magicTable, ::MagicTableMenuScreen)
        event.register(MenuTypeRegistry.instance.matrixDestructor, ::MatrixDestructorScreen)
        event.register(MenuTypeRegistry.instance.enrichmentChamberReceiver, ::EnrichmentChamberReceiverScreen)
        event.register(MenuTypeRegistry.instance.rayTower, ::RayTowerScreen)
    }

    private fun onRegisterClientPayloads(event: RegisterClientPayloadHandlersEvent) {
        event.register(ResearchSyncPayload.TYPE) { payload, _ -> ClientResearchState.apply(payload) }
        event.register(ResearchProgressPayload.TYPE) { payload, _ -> ClientResearchState.apply(payload) }
        event.register(BoundGemTooltipResponsePayload.TYPE) { payload, _ -> BoundGemTooltipNetwork.acceptResponse(payload) }
        event.register(SoulStoneTooltipResponsePayload.TYPE) { payload, _ -> SoulStoneTooltipNetwork.acceptResponse(payload) }
        event.register(GeoBlockAnimationPayload.TYPE) { payload, _ -> ClientGeoAnimations.handle(payload) }
        event.register(GeoEntityAnimationPayload.TYPE) { payload, _ -> ClientGeoAnimations.handle(payload) }
        event.register(GeoItemAnimationPayload.TYPE) { payload, _ -> ClientGeoAnimations.handle(payload) }
        event.register(GeoBlockAnimationStopPayload.TYPE) { payload, _ -> ClientGeoAnimations.handle(payload) }
        event.register(GeoEntityAnimationStopPayload.TYPE) { payload, _ -> ClientGeoAnimations.handle(payload) }
        event.register(GeoItemAnimationStopPayload.TYPE) { payload, _ -> ClientGeoAnimations.handle(payload) }
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
        event.registerBlockEntityRenderer(BlockEntityTypeRegistry.instance.mithrilineFurnace, ::MithrilineFurnaceRenderer)
    }

    private fun onRegisterEntityModelLayer(event: EntityRenderersEvent.RegisterLayerDefinitions) {
        event.registerLayerDefinition(MithrilineFurnaceRenderer.MF_LAYER, MithrilineFurnaceRenderer::createBodyLayer)
    }
}
