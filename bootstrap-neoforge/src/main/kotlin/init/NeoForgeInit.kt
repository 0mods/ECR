package com.algorithmlx.ecr.neoforge.init

import com.algorithmlx.ecr.api.ModId
import com.algorithmlx.ecr.api.ecRL
import com.algorithmlx.ecr.api.init.MultiblockMatcherTypes
import com.algorithmlx.ecr.api.item.HasSubItem
import com.algorithmlx.ecr.api.item.NoTab
import com.algorithmlx.ecr.api.registries.ECRegistries
import com.algorithmlx.ecr.api.research.*
import com.algorithmlx.ecr.api.research.content.ResearchAction
import com.algorithmlx.ecr.api.utils.countByIngredient
import com.algorithmlx.ecr.api.utils.openMenuScreenInternal
import com.algorithmlx.ecr.common.init.config.ConfigManager
import com.algorithmlx.ecr.common.init.config.ECConfig
import com.algorithmlx.ecr.common.init.events.ECEvents
import com.algorithmlx.ecr.common.init.registry.*
import com.algorithmlx.ecr.common.item.NamedBlockItem
import com.algorithmlx.ecr.common.research.ResearchConfigDisabler
import com.algorithmlx.ecr.common.research.ResearchCommands
import com.algorithmlx.ecr.neoforge.api.CountIngredient
import com.algorithmlx.ecr.neoforge.init.registry.*
import com.algorithmlx.ecr.network.FinishCraftParticle
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionResult
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.loading.FMLEnvironment
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.event.AddServerReloadListenersEvent
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent
import net.neoforged.neoforge.event.OnDatapackSyncEvent
import net.neoforged.neoforge.event.RegisterCommandsEvent
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent
import net.neoforged.neoforge.event.tick.PlayerTickEvent
import net.neoforged.neoforge.network.PacketDistributor
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent
import net.neoforged.neoforge.registries.NewRegistryEvent
import net.neoforged.neoforge.resource.ListenerKey
import java.io.File

object NeoForgeInit {
    fun init(bus: IEventBus) {
        ECConfig.instance = ConfigManager.saveOrLoad(File("config/ecr.json"), ECConfig())

        val forgeBus = NeoForge.EVENT_BUS
        ResearchConfigDisabler.init()

        forgeBus.addListener(::onItemTooltip)
        forgeBus.addListener(::onRegisterPayload)

        bus.addListener(::onNewRegistry)
        bus.addListener(::onCreativeTabs)

        bus.addListener(::onAddReloadListener)
        bus.addListener(::onDatapackSync)
        bus.addListener(::onRightClickItemInteract)
        bus.addListener(::onRightClickBlockInteract)
        bus.addListener(::onLeftClickBlock)
        bus.addListener(::onEntityInteract)
        bus.addListener(::onEntityInteractSpecific)
        bus.addListener(::onAttackEntityEvent)
        bus.addListener(::onPlayerTick)
        bus.addListener(::onRegisterCommands)

        initRegistries(bus)

        if (FMLEnvironment.getDist().isClient)
            NeoForgeClientInit.init(bus)

        extendPlatform()
    }

    private fun initRegistries(bus: IEventBus) {
        RecipeSerializerRegistry.instance = NeoForgeRecipeSerializerRegistry(bus)
        RecipeTypeRegistry.instance = NeoForgeRecipeTypeRegistry(bus)
        BlockCodecRegistry.instance = NeoForgeBlockCodecRegistry(bus)
        BookLevelRegistry.instance = NeoForgeBookLevelRegistry(bus)
        NeoForgeResearchSerializerRegistry(bus)
        BlockRegistry.instance = NeoForgeBlockRegistry(bus)
        BlockEntityTypeRegistry.instance = NeoForgeBlockEntityTypeRegistry(bus)
        DataComponentRegistry.instance = NeoForgeDataComponentRegistry(bus)
        ItemRegistry.instance = NeoForgeItemRegistry(bus)
        CreativeTabRegistry.instance = NeoForgeCreativeTabRegistry(bus)
        MenuTypeRegistry.instance = NeoForgeMenuTypeRegistry(bus)
        MRUTypeRegistry.instance = NeoForgeMRUTypeRegistry(bus)
        MultiblockMatcherTypes.instance = NeoForgeMultiblockMatcherTypes(bus)
        MultiblockRegistry.instance = NeoForgeMultiblockRegistry(bus)
        RecipeDisplayTypeRegistry.instance = NeoForgeRecipeDisplayTypeRegistry(bus)
        IngredientRegistry.init(bus)
    }

    private fun onNewRegistry(event: NewRegistryEvent) {
        event.register(ECRegistries.MULTIBLOCK)
        event.register(ECRegistries.MRU_TYPE)
        event.register(ECRegistries.BOOK_TYPES)
        event.register(ECRegistries.BOOK_ELEMENT_SERIALIZER)
        event.register(ECRegistries.RESEARCH_TASK_SERIALIZER)
        event.register(ECRegistries.MULTIBLOCK_MATCHER_TYPE)
    }

    private fun onItemTooltip(event: ItemTooltipEvent) {
        ECEvents.itemTooltip(event.itemStack, event.toolTip)
    }

    private fun onCreativeTabs(event: BuildCreativeModeTabContentsEvent) {
        BuiltInRegistries.ITEM.keySet().filter { it.namespace == ModId }.forEach {
            val item = BuiltInRegistries.ITEM.getOptional(it).get()
            if (event.tab == CreativeTabRegistry.instance.blocks) {
                if ((item is BlockItem || item is NamedBlockItem)  && item.block !is NoTab)
                    event.accept(item)
                return@forEach
            }

            if (BuiltInRegistries.BLOCK.getOptional(it).isPresent) return@forEach

            if (item is NoTab || event.tab != CreativeTabRegistry.instance.items) return@forEach

            if (item is HasSubItem) {
                item.addSubItems(ItemStack(item)).forEach { stack ->
                    event.accept(stack)
                }

                return@forEach
            }

            event.accept(item)
        }
    }

    private fun onRegisterPayload(event: RegisterPayloadHandlersEvent) {
        val registrar = event.registrar(ModId)
        registrar.playToClient(FinishCraftParticle.TYPE, FinishCraftParticle.STREAM_CODEC)

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

    private fun onRegisterCommands(event: RegisterCommandsEvent) {
        ResearchCommands.register(event.dispatcher)
    }

    private fun onAddReloadListener(event: AddServerReloadListenersEvent) {
        event.addRetainedListener(ListenerKey.create("research".ecRL), ResearchReloadListener())
    }

    private fun onDatapackSync(event: OnDatapackSyncEvent) {
        event.relevantPlayers.forEach(ResearchProgress::onPlayerJoin)
    }

    private fun onRightClickItemInteract(event: PlayerInteractEvent.RightClickItem) {
        if (!ResearchAccess.canAccess(event.entity, event.itemStack, ResearchAction.USE)) {
            event.isCanceled = true
            event.cancellationResult = InteractionResult.FAIL
        }
    }

    private fun onRightClickBlockInteract(event: PlayerInteractEvent.RightClickBlock) {
        val blockAllowed = ResearchAccess.canAccess(event.entity, event.level.getBlockState(event.pos), ResearchAction.INTERACT)
        val action = if (event.itemStack.item is BlockItem) ResearchAction.PLACE else ResearchAction.USE
        val itemAllowed = ResearchAccess.canAccess(event.entity, event.itemStack, action)
        if (!blockAllowed || !itemAllowed) {
            event.isCanceled = true
            event.cancellationResult = InteractionResult.FAIL
        }
    }

    private fun onLeftClickBlock(event: PlayerInteractEvent.LeftClickBlock) {
        val blockAllowed = ResearchAccess.canAccess(event.entity, event.level.getBlockState(event.pos), ResearchAction.BREAK)
        val itemAllowed = ResearchAccess.canAccess(event.entity, event.itemStack, ResearchAction.ATTACK)
        if (!blockAllowed || !itemAllowed) event.isCanceled = true
    }

    private fun onEntityInteract(event: PlayerInteractEvent.EntityInteract) {
        if (!allowEntityInteraction(event)) {
            event.isCanceled = true
            event.cancellationResult = InteractionResult.FAIL
        }
    }

    private fun onEntityInteractSpecific(event: PlayerInteractEvent.EntityInteractSpecific) {
        if (!allowEntityInteraction(event)) {
            event.isCanceled = true
            event.cancellationResult = InteractionResult.FAIL
        }
    }

    private fun onAttackEntityEvent(event: AttackEntityEvent) {
        val entityAllowed = ResearchAccess.canAccess(event.entity, event.target, ResearchAction.ATTACK)
        val itemAllowed = ResearchAccess.canAccess(event.entity, event.entity.mainHandItem, ResearchAction.ATTACK)
        if (!entityAllowed || !itemAllowed) event.isCanceled = true
    }

    private fun onPlayerTick(event: PlayerTickEvent.Post) {
        (event.entity as? ServerPlayer)?.let(ResearchProgress::tick)
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

    private fun extendPlatform() {
        ResearchNetwork.sendToPlayer = { player, payload -> PacketDistributor.sendToPlayer(player, payload) }
        ResearchNetwork.sendProgressToPlayer = { player, payload -> PacketDistributor.sendToPlayer(player, payload) }

        countByIngredient = { (it.customIngredient as? CountIngredient)?.count ?: 1 }

        openMenuScreenInternal = { player: Player, provider: MenuProvider, _: Level, pos: BlockPos ->
            player.openMenu(provider, pos)
        }
    }
}
