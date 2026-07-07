package com.algorithmlx.ecr.neoforge.research

import com.algorithmlx.ecr.api.ModId
import com.algorithmlx.ecr.api.ecRL
import com.algorithmlx.ecr.api.research.*
import com.algorithmlx.ecr.api.research.content.ResearchAction
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.BlockItem
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.event.AddServerReloadListenersEvent
import net.neoforged.neoforge.event.OnDatapackSyncEvent
import net.neoforged.neoforge.event.RegisterCommandsEvent
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent
import net.neoforged.neoforge.event.tick.PlayerTickEvent
import net.neoforged.neoforge.network.PacketDistributor
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent
import net.neoforged.neoforge.resource.ListenerKey
import com.algorithmlx.ecr.common.research.ResearchCommands

object NeoForgeResearch {
    fun init(modBus: IEventBus) {
        modBus.addListener(::registerPayloads)
        NeoForge.EVENT_BUS.addListener(::addReloadListener)
        NeoForge.EVENT_BUS.addListener(::syncData)
        NeoForge.EVENT_BUS.addListener(::rightClickItem)
        NeoForge.EVENT_BUS.addListener(::rightClickBlock)
        NeoForge.EVENT_BUS.addListener(::leftClickBlock)
        NeoForge.EVENT_BUS.addListener(::entityInteract)
        NeoForge.EVENT_BUS.addListener(::entityInteractSpecific)
        NeoForge.EVENT_BUS.addListener(::attackEntity)
        NeoForge.EVENT_BUS.addListener(::playerTick)
        NeoForge.EVENT_BUS.addListener(::registerCommands)
        ResearchNetwork.sendToPlayer = { player, payload -> PacketDistributor.sendToPlayer(player, payload) }
        ResearchNetwork.sendProgressToPlayer = { player, payload -> PacketDistributor.sendToPlayer(player, payload) }
    }

    private fun registerCommands(event: RegisterCommandsEvent) {
        ResearchCommands.register(event.dispatcher)
    }

    private fun registerPayloads(event: RegisterPayloadHandlersEvent) {
        val registrar = event.registrar(ModId)
        registrar.playToClient(ResearchSyncPayload.TYPE, ResearchSyncPayload.STREAM_CODEC)
        registrar.playToClient(ResearchProgressPayload.TYPE, ResearchProgressPayload.STREAM_CODEC)
        registrar.playToServer(CompleteResearchPayload.TYPE, CompleteResearchPayload.STREAM_CODEC) { payload, context ->
            context.enqueueWork {
                val player = context.player() as? ServerPlayer ?: return@enqueueWork
                ResearchProgress.tryUnlock(player, payload.research)
            }
        }
        registrar.playToServer(FavoriteResearchPayload.TYPE, FavoriteResearchPayload.STREAM_CODEC) { payload, context ->
            context.enqueueWork {
                val player = context.player() as? ServerPlayer ?: return@enqueueWork
                ResearchProgress.setBookmark(player, payload.research, payload.spread, payload.color)
            }
        }
        registrar.playToServer(UpdateBookViewPayload.TYPE, UpdateBookViewPayload.STREAM_CODEC) { payload, context ->
            context.enqueueWork {
                val player = context.player() as? ServerPlayer ?: return@enqueueWork
                ResearchProgress.updateView(player, payload.state)
            }
        }
    }

    private fun addReloadListener(event: AddServerReloadListenersEvent) {
        event.addRetainedListener(ListenerKey.create("research".ecRL), ResearchReloadListener())
    }

    private fun syncData(event: OnDatapackSyncEvent) {
        event.relevantPlayers.forEach(ResearchProgress::onPlayerJoin)
    }

    private fun rightClickItem(event: PlayerInteractEvent.RightClickItem) {
        if (!ResearchAccess.canAccess(event.entity, event.itemStack, ResearchAction.USE)) {
            event.isCanceled = true
            event.cancellationResult = InteractionResult.FAIL
        }
    }

    private fun rightClickBlock(event: PlayerInteractEvent.RightClickBlock) {
        val blockAllowed = ResearchAccess.canAccess(event.entity, event.level.getBlockState(event.pos), ResearchAction.INTERACT)
        val action = if (event.itemStack.item is BlockItem) ResearchAction.PLACE else ResearchAction.USE
        val itemAllowed = ResearchAccess.canAccess(event.entity, event.itemStack, action)
        if (!blockAllowed || !itemAllowed) {
            event.isCanceled = true
            event.cancellationResult = InteractionResult.FAIL
        }
    }

    private fun leftClickBlock(event: PlayerInteractEvent.LeftClickBlock) {
        val blockAllowed = ResearchAccess.canAccess(event.entity, event.level.getBlockState(event.pos), ResearchAction.BREAK)
        val itemAllowed = ResearchAccess.canAccess(event.entity, event.itemStack, ResearchAction.ATTACK)
        if (!blockAllowed || !itemAllowed) event.isCanceled = true
    }

    private fun entityInteract(event: PlayerInteractEvent.EntityInteract) {
        if (!allowEntityInteraction(event)) {
            event.isCanceled = true
            event.cancellationResult = InteractionResult.FAIL
        }
    }

    private fun entityInteractSpecific(event: PlayerInteractEvent.EntityInteractSpecific) {
        if (!allowEntityInteraction(event)) {
            event.isCanceled = true
            event.cancellationResult = InteractionResult.FAIL
        }
    }

    private fun allowEntityInteraction(event: PlayerInteractEvent): Boolean {
        val target = when (event) {
            is PlayerInteractEvent.EntityInteract -> event.target
            is PlayerInteractEvent.EntityInteractSpecific -> event.target
            else -> return true
        }
        return ResearchAccess.canAccess(event.entity, target, ResearchAction.INTERACT) &&
            ResearchAccess.canAccess(event.entity, event.itemStack, ResearchAction.USE)
    }

    private fun attackEntity(event: AttackEntityEvent) {
        val entityAllowed = ResearchAccess.canAccess(event.entity, event.target, ResearchAction.ATTACK)
        val itemAllowed = ResearchAccess.canAccess(event.entity, event.entity.mainHandItem, ResearchAction.ATTACK)
        if (!entityAllowed || !itemAllowed) event.isCanceled = true
    }

    private fun playerTick(event: PlayerTickEvent.Post) {
        (event.entity as? ServerPlayer)?.let(ResearchProgress::tick)
    }
}
