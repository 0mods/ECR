package com.algorithmlx.ecr.fabric.init

import com.algorithmlx.ecr.api.research.ClientResearchState
import com.algorithmlx.ecr.api.research.CompleteResearchPayload
import com.algorithmlx.ecr.api.research.FavoriteResearchPayload
import com.algorithmlx.ecr.api.research.ResearchNetwork
import com.algorithmlx.ecr.api.research.ResearchProgressPayload
import com.algorithmlx.ecr.api.research.ResearchSyncPayload
import com.algorithmlx.ecr.api.research.UpdateBookViewPayload
import com.algorithmlx.ecr.api.particle.BedrockParticleRenderTypes
import com.algorithmlx.ecr.api.particle.BedrockParticles
import com.algorithmlx.ecr.api.particle.ClientParticleSystems
import com.algorithmlx.ecr.client.book.ResearchBookClient
import com.algorithmlx.ecr.client.renderer.MatrixDestructorRenderer
import com.algorithmlx.ecr.client.renderer.MithrilineFurnaceRenderer
import com.algorithmlx.ecr.client.screen.MagicTableMenuScreen
import com.algorithmlx.ecr.client.screen.MatrixDestructorScreen
import com.algorithmlx.ecr.client.screen.MithrilineFurnaceScreen
import com.algorithmlx.ecr.registry.BlockEntityTypeRegistry
import com.algorithmlx.ecr.registry.MenuTypeRegistry
import com.algorithmlx.ecr.fabric.client.MultiblockPreviewGuiBridgeInit
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
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.server.packs.PackType
import com.algorithmlx.ecr.api.utils.rl
import kotlin.random.Random

object FabricClientInit {
    @JvmStatic
    fun init() {
        registerBedrockParticles()
        registerReceivers()

        MultiblockPreviewGuiBridgeInit.init()
        ResearchBookClient.init()

        BlockEntityRenderers.register(BlockEntityTypeRegistry.mithrilineFurnace, ::MithrilineFurnaceRenderer)
        BlockEntityRenderers.register(BlockEntityTypeRegistry.matrixDestructor, ::MatrixDestructorRenderer)

        ModelLayerRegistry.registerModelLayer(MithrilineFurnaceRenderer.MF_LAYER, MithrilineFurnaceRenderer::createBodyLayer)

        MenuScreens.register(MenuTypeRegistry.mithrilineFurnace, ::MithrilineFurnaceScreen)
        MenuScreens.register(MenuTypeRegistry.magicTable, ::MagicTableMenuScreen)
        MenuScreens.register(MenuTypeRegistry.matrixDestructor, ::MatrixDestructorScreen)
    }

    private fun registerBedrockParticles() {
        BedrockParticleRenderTypes.init()
        ResourceLoader.get(PackType.CLIENT_RESOURCES)
            .registerReloadListener("ecreimagined:bedrock_particles".rl, BedrockParticles)
        ClientTickEvents.END_LEVEL_TICK.register { level ->
            ClientParticleSystems.get(level)?.update()
        }
        LevelRenderEvents.COLLECT_SUBMITS.register { context ->
            val minecraft = Minecraft.getInstance()
            val level = minecraft.level ?: return@register
            val poseStack = context.poseStack() ?: return@register
            ClientParticleSystems.get(level)?.submit(
                poseStack,
                context.submitNodeCollector(),
                context.levelState(),
                minecraft.player?.uuid,
                minecraft.options.cameraType.isFirstPerson,
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
        BoundGemTooltipNetwork.sendRequestToServer = { payload ->
            runCatching {
                if (ClientPlayNetworking.canSend(BoundGemTooltipRequestPayload.TYPE)) {
                    ClientPlayNetworking.send(payload)
                }
            }
        }
    }
}
