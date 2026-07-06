package com.algorithmlx.ecr.fabric.research

import com.algorithmlx.ecr.api.ecRL
import com.algorithmlx.ecr.api.research.*
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.event.player.*
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.fabricmc.fabric.api.resource.v1.ResourceLoader
import net.minecraft.server.packs.PackType
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.BlockItem
import com.algorithmlx.ecr.common.research.ResearchCommands

object FabricResearch {
    fun init() {
        registerPayloads()
        registerReloadListener()
        registerProgressEvents()
        registerAccessEvents()
        CommandRegistrationCallback.EVENT.register { dispatcher, _, _ -> ResearchCommands.register(dispatcher) }
        ResearchNetwork.sendToPlayer = { player, payload -> ServerPlayNetworking.send(player, payload) }
        ResearchNetwork.sendProgressToPlayer = { player, payload -> ServerPlayNetworking.send(player, payload) }
    }

    private fun registerPayloads() {
        PayloadTypeRegistry.clientboundPlay().registerLarge(ResearchSyncPayload.TYPE, ResearchSyncPayload.STREAM_CODEC, 8 * 1024 * 1024)
        PayloadTypeRegistry.clientboundPlay().register(ResearchProgressPayload.TYPE, ResearchProgressPayload.STREAM_CODEC)
        PayloadTypeRegistry.serverboundPlay().register(CompleteResearchPayload.TYPE, CompleteResearchPayload.STREAM_CODEC)
        PayloadTypeRegistry.serverboundPlay().register(FavoriteResearchPayload.TYPE, FavoriteResearchPayload.STREAM_CODEC)
        PayloadTypeRegistry.serverboundPlay().register(UpdateBookViewPayload.TYPE, UpdateBookViewPayload.STREAM_CODEC)
        ServerPlayNetworking.registerGlobalReceiver(CompleteResearchPayload.TYPE) { payload, context ->
            context.server().execute { ResearchProgress.tryUnlock(context.player(), payload.research) }
        }
        ServerPlayNetworking.registerGlobalReceiver(FavoriteResearchPayload.TYPE) { payload, context ->
            context.server().execute { ResearchProgress.setBookmark(context.player(), payload.research, payload.spread, payload.color) }
        }
        ServerPlayNetworking.registerGlobalReceiver(UpdateBookViewPayload.TYPE) { payload, context ->
            context.server().execute { ResearchProgress.updateView(context.player(), payload.state) }
        }
    }

    private fun registerReloadListener() {
        ResourceLoader.get(PackType.SERVER_DATA).registerReloadListener("research".ecRL, ResearchReloadListener())
    }

    private fun registerProgressEvents() {
        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register { player, _ -> ResearchProgress.onPlayerJoin(player) }
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register { server, _, success ->
            if (success) ResearchProgress.syncAll(server)
        }
        ServerTickEvents.END_SERVER_TICK.register { server -> server.playerList.players.forEach(ResearchProgress::tick) }
    }

    private fun registerAccessEvents() {
        UseItemCallback.EVENT.register { player, _, hand ->
            if (ResearchAccess.canAccess(player, player.getItemInHand(hand), ResearchAction.USE)) InteractionResult.PASS else InteractionResult.FAIL
        }
        UseBlockCallback.EVENT.register { player, level, hand, hit ->
            val blockAllowed = ResearchAccess.canAccess(player, level.getBlockState(hit.blockPos), ResearchAction.INTERACT)
            val stack = player.getItemInHand(hand)
            val action = if (stack.item is BlockItem) ResearchAction.PLACE else ResearchAction.USE
            val itemAllowed = ResearchAccess.canAccess(player, stack, action)
            if (blockAllowed && itemAllowed) InteractionResult.PASS else InteractionResult.FAIL
        }
        AttackBlockCallback.EVENT.register { player, level, hand, pos, _ ->
            val blockAllowed = ResearchAccess.canAccess(player, level.getBlockState(pos), ResearchAction.BREAK)
            val itemAllowed = ResearchAccess.canAccess(player, player.getItemInHand(hand), ResearchAction.ATTACK)
            if (blockAllowed && itemAllowed) InteractionResult.PASS else InteractionResult.FAIL
        }
        UseEntityCallback.EVENT.register { player, _, hand, entity, _ ->
            val entityAllowed = ResearchAccess.canAccess(player, entity, ResearchAction.INTERACT)
            val itemAllowed = ResearchAccess.canAccess(player, player.getItemInHand(hand), ResearchAction.USE)
            if (entityAllowed && itemAllowed) InteractionResult.PASS else InteractionResult.FAIL
        }
        AttackEntityCallback.EVENT.register { player, _, hand, entity, _ ->
            val entityAllowed = ResearchAccess.canAccess(player, entity, ResearchAction.ATTACK)
            val itemAllowed = ResearchAccess.canAccess(player, player.getItemInHand(hand), ResearchAction.ATTACK)
            if (entityAllowed && itemAllowed) InteractionResult.PASS else InteractionResult.FAIL
        }
    }
}
