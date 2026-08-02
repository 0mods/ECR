package com.algorithmlx.ecr.fabric.init

import com.algorithmlx.ecr.api.research.ClientResearchState
import com.algorithmlx.ecr.api.research.CompleteResearchPayload
import com.algorithmlx.ecr.api.research.FavoriteResearchPayload
import com.algorithmlx.ecr.api.research.ResearchNetwork
import com.algorithmlx.ecr.api.research.ResearchProgressPayload
import com.algorithmlx.ecr.api.research.ResearchSyncPayload
import com.algorithmlx.ecr.api.research.UpdateBookViewPayload
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
import com.algorithmlx.ecr.registry.BlockEntityTypeRegistry
import com.algorithmlx.ecr.registry.MenuTypeRegistry
import com.algorithmlx.ecr.fabric.client.MultiblockPreviewGuiBridgeInit
import com.algorithmlx.ecr.fabric.client.FabricConnectedTextures
import com.algorithmlx.ecr.client.ECRConnectedTextures
import com.algorithmlx.ecr.network.BoundGemTooltipNetwork
import com.algorithmlx.ecr.network.BoundGemTooltipRequestPayload
import com.algorithmlx.ecr.network.BoundGemTooltipResponsePayload
import com.algorithmlx.ecr.network.FinishCraftParticle
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents
import net.fabricmc.fabric.api.resource.v1.ResourceLoader
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.MenuScreens
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers
import net.minecraft.client.renderer.special.SpecialModelRenderers
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.server.packs.PackType
import com.algorithmlx.ecr.api.utils.rl
import com.algorithmlx.ecr.client.screen.EnrichmentChamberControllerScreen
import com.algorithmlx.ecr.client.screen.EnrichmentChamberReceiverScreen
import kotlin.random.Random

object FabricClientInit {
    @JvmStatic
    fun init() {
        SpecialModelRenderers.ID_MAPPER.put(BedrockGeoItemRenderer.ID, BedrockGeoItemRenderer.Unbaked.CODEC)
        FabricConnectedTextures.init()
        ECRConnectedTextures.init()
        registerBedrockParticles()
        registerReceivers()

        MultiblockPreviewGuiBridgeInit.init()
        ResearchBookClient.init()

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

        ModelLayerRegistry.registerModelLayer(MithrilineFurnaceRenderer.MF_LAYER, MithrilineFurnaceRenderer::createBodyLayer)

        MenuScreens.register(MenuTypeRegistry.instance.mithrilineFurnace, ::MithrilineFurnaceScreen)
        MenuScreens.register(MenuTypeRegistry.instance.magicTable, ::MagicTableMenuScreen)
        MenuScreens.register(MenuTypeRegistry.instance.matrixDestructor, ::MatrixDestructorScreen)
        MenuScreens.register(MenuTypeRegistry.instance.enrichmentChamberController, ::EnrichmentChamberControllerScreen)
        MenuScreens.register(MenuTypeRegistry.instance.enrichmentChamberReceiver, ::EnrichmentChamberReceiverScreen)
        MenuScreens.register(MenuTypeRegistry.instance.rayTower, ::RayTowerScreen)
    }

    private fun registerBedrockParticles() {
        BedrockParticleRenderTypes.init()
        ResourceLoader.get(PackType.CLIENT_RESOURCES)
            .registerReloadListener("bedrock_particles".ecRL, BedrockParticles)
        ResourceLoader.get(PackType.CLIENT_RESOURCES)
            .registerReloadListener("bedrock_geo".ecRL, BedrockGeoAssets)
        ClientTickEvents.END_LEVEL_TICK.register { level ->
            ClientParticleSystems.get(level)?.update()
        }
        LevelRenderEvents.COLLECT_SUBMITS.register { context ->
            val minecraft = Minecraft.getInstance()
            val level = minecraft.level ?: return@register
            val poseStack = context.poseStack()
            ClientParticleSystems.get(level)?.submit(
                poseStack,
                context.submitNodeCollector(),
                context.levelState(),
                minecraft.deltaTracker.getGameTimeDeltaPartialTick(false),
                minecraft.player?.uuid,
                minecraft.options.cameraType.isFirstPerson,
            )
            BoundGemLinkRenderer.submit(
                poseStack,
                context.submitNodeCollector(),
                context.levelState()
            )
        }
    }

    private fun registerReceivers() {
        ClientPlayNetworking.registerGlobalReceiver(FinishCraftParticle.TYPE) { data, context ->
            val level = context.client().level ?: return@registerGlobalReceiver

            (0 ..< data.count).forEach { _ ->
                level.addParticle(
                    ParticleTypes.POOF,
                    data.x,
                    data.y + Random.nextDouble(0.15, 0.6),
                    data.z,
                    Random.nextDouble(-0.06, 0.06),
                    Random.nextDouble(0.0, 0.15),
                    Random.nextDouble(-0.06, 0.06)
                )
            }
        }
        ClientPlayNetworking.registerGlobalReceiver(GeoBlockAnimationPayload.TYPE) { payload, context ->
            context.client().execute { ClientGeoAnimations.handle(payload) }
        }
        ClientPlayNetworking.registerGlobalReceiver(GeoEntityAnimationPayload.TYPE) { payload, context ->
            context.client().execute { ClientGeoAnimations.handle(payload) }
        }
        ClientPlayNetworking.registerGlobalReceiver(GeoItemAnimationPayload.TYPE) { payload, context ->
            context.client().execute { ClientGeoAnimations.handle(payload) }
        }
        ClientPlayNetworking.registerGlobalReceiver(GeoBlockAnimationStopPayload.TYPE) { payload, context ->
            context.client().execute { ClientGeoAnimations.handle(payload) }
        }
        ClientPlayNetworking.registerGlobalReceiver(GeoEntityAnimationStopPayload.TYPE) { payload, context ->
            context.client().execute { ClientGeoAnimations.handle(payload) }
        }
        ClientPlayNetworking.registerGlobalReceiver(GeoItemAnimationStopPayload.TYPE) { payload, context ->
            context.client().execute { ClientGeoAnimations.handle(payload) }
        }

        ClientPlayNetworking.registerGlobalReceiver(ResearchSyncPayload.TYPE) { payload, context ->
            context.client().execute { ClientResearchState.apply(payload) }
        }
        ClientPlayNetworking.registerGlobalReceiver(ResearchProgressPayload.TYPE) { payload, context ->
            context.client().execute { ClientResearchState.apply(payload) }
        }
        ClientPlayNetworking.registerGlobalReceiver(BoundGemTooltipResponsePayload.TYPE) { payload, context ->
            context.client().execute { BoundGemTooltipNetwork.acceptResponse(payload) }
        }

        ResearchNetwork.completeResearch = { ClientPlayNetworking.send(CompleteResearchPayload(it)) }
        ResearchNetwork.updateFavorite = { research, spread, color -> ClientPlayNetworking.send(FavoriteResearchPayload(research, spread, color)) }
        ResearchNetwork.updateView = { state ->
            runCatching {
                if (ClientPlayNetworking.canSend(UpdateBookViewPayload.TYPE)) {
                    ClientPlayNetworking.send(UpdateBookViewPayload(state))
                }
            }
        }
        BoundGemTooltipNetwork.currentDimension = { Minecraft.getInstance().level?.dimension() }
        GeoAnimationNetwork.playClientBlockAnimation = ClientGeoAnimations::handle
        GeoAnimationNetwork.playClientEntityAnimation = ClientGeoAnimations::handle
        GeoAnimationNetwork.playClientItemAnimation = ClientGeoAnimations::handle
        GeoAnimationNetwork.stopClientBlockAnimation = ClientGeoAnimations::handle
        GeoAnimationNetwork.stopClientEntityAnimation = ClientGeoAnimations::handle
        GeoAnimationNetwork.stopClientItemAnimation = ClientGeoAnimations::handle
        BoundGemTooltipNetwork.sendRequestToServer = { payload ->
            runCatching {
                if (ClientPlayNetworking.canSend(BoundGemTooltipRequestPayload.TYPE)) {
                    ClientPlayNetworking.send(payload)
                }
            }
        }
    }
}
