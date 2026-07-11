package com.algorithmlx.ecr.neoforge.init

import com.algorithmlx.ecr.api.client.render.MultiblockPreviewGuiBridge
import com.algorithmlx.ecr.api.client.render.MultiblockPreviewPictureRenderer
import com.algorithmlx.ecr.api.client.render.MultiblockPreviewRenderState
import com.algorithmlx.ecr.api.research.ClientResearchState
import com.algorithmlx.ecr.api.research.CompleteResearchPayload
import com.algorithmlx.ecr.api.research.FavoriteResearchPayload
import com.algorithmlx.ecr.api.research.ResearchNetwork
import com.algorithmlx.ecr.api.research.ResearchProgressPayload
import com.algorithmlx.ecr.api.research.ResearchSyncPayload
import com.algorithmlx.ecr.api.research.UpdateBookViewPayload
import com.algorithmlx.ecr.client.book.ResearchBookClient
import com.algorithmlx.ecr.client.screen.MithrilineFurnaceScreen
import com.algorithmlx.ecr.common.init.registry.MenuTypeRegistry
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent
import net.neoforged.neoforge.client.event.RegisterPictureInPictureRenderersEvent
import net.neoforged.neoforge.client.network.ClientPacketDistributor
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent

object NeoForgeClientInit {
    fun init(bus: IEventBus) {
        MultiblockPreviewGuiBridge.install(GuiGraphicsExtractor::submitPictureInPictureRenderState)
        bus.addListener(::registerPIPRenders)

        bus.addListener(::registerClientPayloads)
        bus.addListener(::onClientInit)
        bus.addListener(::onMenuScreen)
    }

    private fun onClientInit(event: FMLClientSetupEvent) {
        event.enqueueWork {
            ResearchBookClient.init()
            ResearchNetwork.completeResearch = { ClientPacketDistributor.sendToServer(CompleteResearchPayload(it)) }
            ResearchNetwork.updateFavorite = { research, spread, color -> ClientPacketDistributor.sendToServer(FavoriteResearchPayload(research, spread, color)) }
            ResearchNetwork.updateView = { state -> runCatching { ClientPacketDistributor.sendToServer(UpdateBookViewPayload(state)) } }
        }
    }

    private fun onMenuScreen(event: RegisterMenuScreensEvent) {
        event.register(MenuTypeRegistry.instance.mithrilineFurnace, ::MithrilineFurnaceScreen)
    }

    private fun registerClientPayloads(event: RegisterClientPayloadHandlersEvent) {
        event.register(ResearchSyncPayload.TYPE) { payload, _ ->
            ClientResearchState.apply(payload)
        }
        event.register(ResearchProgressPayload.TYPE) { payload, _ ->
            ClientResearchState.apply(payload)
        }
    }

    private fun registerPIPRenders(event: RegisterPictureInPictureRenderersEvent) {
        event.register(MultiblockPreviewRenderState::class.java, ::MultiblockPreviewPictureRenderer)
    }
}
