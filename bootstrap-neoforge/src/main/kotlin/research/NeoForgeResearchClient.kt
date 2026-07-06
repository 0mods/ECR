package com.algorithmlx.ecr.neoforge.research

import com.algorithmlx.ecr.api.research.*
import com.algorithmlx.ecr.client.book.ResearchBookClient
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.client.network.ClientPacketDistributor
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent

object NeoForgeResearchClient {
    fun init(modBus: IEventBus) {
        ResearchBookClient.init()
        modBus.addListener(::registerClientPayloads)
        ResearchNetwork.completeResearch = { ClientPacketDistributor.sendToServer(CompleteResearchPayload(it)) }
        ResearchNetwork.updateFavorite = { research, spread, color -> ClientPacketDistributor.sendToServer(FavoriteResearchPayload(research, spread, color)) }
        ResearchNetwork.updateView = { state -> runCatching { ClientPacketDistributor.sendToServer(UpdateBookViewPayload(state)) } }
    }

    private fun registerClientPayloads(event: RegisterClientPayloadHandlersEvent) {
        event.register(ResearchSyncPayload.TYPE) { payload, context ->
            context.enqueueWork { ClientResearchState.apply(payload) }
        }
        event.register(ResearchProgressPayload.TYPE) { payload, context ->
            context.enqueueWork { ClientResearchState.apply(payload) }
        }
    }
}
