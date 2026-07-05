package com.algorithmlx.ecr.fabric.research

import com.algorithmlx.ecr.api.research.*
import com.algorithmlx.ecr.client.book.ResearchBookClient
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking

object FabricResearchClient {
    fun init() {
        ResearchBookClient.init()
        ClientPlayNetworking.registerGlobalReceiver(ResearchSyncPayload.TYPE) { payload, context ->
            context.client().execute { ClientResearchState.apply(payload) }
        }
        ClientPlayNetworking.registerGlobalReceiver(ResearchProgressPayload.TYPE) { payload, context ->
            context.client().execute { ClientResearchState.apply(payload) }
        }
        ResearchNetwork.completeResearch = { ClientPlayNetworking.send(CompleteResearchPayload(it)) }
        ResearchNetwork.updateFavorite = { research, color -> ClientPlayNetworking.send(FavoriteResearchPayload(research, color)) }
        ResearchNetwork.updateView = { state ->
            runCatching {
                if (ClientPlayNetworking.canSend(UpdateBookViewPayload.TYPE)) {
                    ClientPlayNetworking.send(UpdateBookViewPayload(state))
                }
            }
        }
    }
}
