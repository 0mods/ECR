package com.algorithmlx.ecr.fabric.init

import com.algorithmlx.ecr.api.research.ClientResearchState
import com.algorithmlx.ecr.api.research.CompleteResearchPayload
import com.algorithmlx.ecr.api.research.FavoriteResearchPayload
import com.algorithmlx.ecr.api.research.ResearchNetwork
import com.algorithmlx.ecr.api.research.ResearchProgressPayload
import com.algorithmlx.ecr.api.research.ResearchSyncPayload
import com.algorithmlx.ecr.api.research.UpdateBookViewPayload
import com.algorithmlx.ecr.client.book.ResearchBookClient
import com.algorithmlx.ecr.client.renderer.MatrixDestructorRenderer
import com.algorithmlx.ecr.client.renderer.MithrilineFurnaceRenderer
import com.algorithmlx.ecr.client.screen.EnvoyerMenuScreen
import com.algorithmlx.ecr.client.screen.MatrixDestructorScreen
import com.algorithmlx.ecr.client.screen.MithrilineFurnaceScreen
import com.algorithmlx.ecr.common.init.registry.BlockEntityTypeRegistry
import com.algorithmlx.ecr.common.init.registry.MenuTypeRegistry
import com.algorithmlx.ecr.fabric.client.MultiblockPreviewGuiBridgeInit
import com.algorithmlx.ecr.network.BoundGemTooltipNetwork
import com.algorithmlx.ecr.network.BoundGemTooltipRequestPayload
import com.algorithmlx.ecr.network.BoundGemTooltipResponsePayload
import com.algorithmlx.ecr.network.FinishCraftParticle
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.MenuScreens
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers
import net.minecraft.core.particles.ParticleTypes
import kotlin.random.Random

object FabricClientInit {
    @JvmStatic
    fun init() {
        registerReceivers()

        MultiblockPreviewGuiBridgeInit.init()
        ResearchBookClient.init()

        BlockEntityRenderers.register(BlockEntityTypeRegistry.instance.mithrilineFurnace, ::MithrilineFurnaceRenderer)
        BlockEntityRenderers.register(BlockEntityTypeRegistry.instance.matrixDestructor, ::MatrixDestructorRenderer)

        ModelLayerRegistry.registerModelLayer(MithrilineFurnaceRenderer.MF_LAYER, MithrilineFurnaceRenderer::createBodyLayer)

        MenuScreens.register(MenuTypeRegistry.instance.mithrilineFurnace, ::MithrilineFurnaceScreen)
        MenuScreens.register(MenuTypeRegistry.instance.envoyer, ::EnvoyerMenuScreen)
        MenuScreens.register(MenuTypeRegistry.instance.matrixDestructor, ::MatrixDestructorScreen)
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
