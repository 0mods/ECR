package com.algorithmlx.ecr.fabric.init

import com.algorithmlx.ecr.api.ModId
import com.algorithmlx.ecr.api.ecRL
import com.algorithmlx.ecr.api.init.MultiblockMatcherTypes
import com.algorithmlx.ecr.api.item.BoundGem
import com.algorithmlx.ecr.api.item.HasSubItem
import com.algorithmlx.ecr.api.item.NoTab
import com.algorithmlx.ecr.api.menu.MenuTypeData
import com.algorithmlx.ecr.api.mru.MRUDevice
import com.algorithmlx.ecr.api.registries.*
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
import com.algorithmlx.ecr.fabric.api.CountIngredient
import com.algorithmlx.ecr.fabric.init.registry.*
import com.algorithmlx.ecr.network.FinishCraftParticle
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.fabricmc.fabric.api.event.player.AttackBlockCallback
import net.fabricmc.fabric.api.event.player.AttackEntityCallback
import net.fabricmc.fabric.api.event.player.UseBlockCallback
import net.fabricmc.fabric.api.event.player.UseEntityCallback
import net.fabricmc.fabric.api.event.player.UseItemCallback
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuProvider
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer
import net.fabricmc.fabric.api.resource.v1.ResourceLoader
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceKey
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.packs.PackType
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.ItemStack
import java.io.File

object FabricInit {
    @JvmStatic
    fun init() {
        ECConfig.instance = ConfigManager.saveOrLoad(File("config/ecr.json"), ECConfig())

        initBuiltinRegistries()
        ResearchConfigDisabler.init()

        registerPayloads()
        registerReloadListener()
        registerProgressEvents()
        registerAccessEvents()
        tooltipEvent()
        tabEvent()
        boundGemEvents()

        initRegistries()

        CommandRegistrationCallback.EVENT.register { dispatcher, _, _ -> ResearchCommands.register(dispatcher) }

        extendPlatform()
    }

    private fun initRegistries() {
        DataComponentRegistry.instance = FabricDataComponentRegistry
        BlockCodecRegistry.instance = FabricBlockCodecRegistry
        BookLevelRegistry.instance = FabricBookLevelRegistry
        FabricResearchSerializerRegistry.register()
        BlockRegistry.instance = FabricBlockRegistry
        BlockEntityTypeRegistry.instance = FabricBlockEntityTypeRegistry
        ItemRegistry.instance = FabricItemRegistry
        MenuTypeRegistry.instance = FabricMenuTypeRegistry
        MRUTypeRegistry.instance = FabricMRUTypeRegistry
        MultiblockMatcherTypes.instance = FabricMultiblockMatcherTypes
        MultiblockRegistry.instance = FabricMultiblockRegistry
        RecipeDisplayTypeRegistry.instance = FabricRecipeDisplayTypeRegistry
        RecipeSerializerRegistry.instance = FabricRecipeSerializerRegistry
        RecipeTypeRegistry.instance = FabricRecipeTypeRegistry
        CreativeTabRegistry.instance = FabricCreativeTabRegistry
        CustomIngredientSerializer.register(CountIngredient.SERIALIZER)
    }

    private fun initBuiltinRegistries() {
        register(ECRegistryKeys.MRU_TYPE_KEY, ECRegistries.MRU_TYPE)
        register(ECRegistryKeys.MULTIBLOCK_KEY, ECRegistries.MULTIBLOCK)
        register(ECRegistryKeys.BOOK_TYPE_KEY, ECRegistries.BOOK_TYPES)
        register(ECRegistryKeys.BOOK_ELEMENT_SERIALIZER_KEY, ECRegistries.BOOK_ELEMENT_SERIALIZER)
        register(ECRegistryKeys.RESEARCH_TASK_SERIALIZER_KEY, ECRegistries.RESEARCH_TASK_SERIALIZER)
        register(ECRegistryKeys.MULTIBLOCK_MATCHER_TYPE_KEY, ECRegistries.MULTIBLOCK_MATCHER_TYPE)
    }

    private fun registerPayloads() {
        PayloadTypeRegistry.clientboundPlay().registerLarge(ResearchSyncPayload.TYPE, ResearchSyncPayload.STREAM_CODEC, 8 * 1024 * 1024)
        PayloadTypeRegistry.clientboundPlay().register(ResearchProgressPayload.TYPE, ResearchProgressPayload.STREAM_CODEC)
        PayloadTypeRegistry.serverboundPlay().register(CompleteResearchPayload.TYPE, CompleteResearchPayload.STREAM_CODEC)
        PayloadTypeRegistry.serverboundPlay().register(FavoriteResearchPayload.TYPE, FavoriteResearchPayload.STREAM_CODEC)
        PayloadTypeRegistry.serverboundPlay().register(UpdateBookViewPayload.TYPE, UpdateBookViewPayload.STREAM_CODEC)
        PayloadTypeRegistry.clientboundPlay().register(
            FinishCraftParticle.TYPE,
            FinishCraftParticle.STREAM_CODEC
        )

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

    private fun tooltipEvent() {
        ItemTooltipCallback.EVENT.register { stack, _, _, components ->
            ECEvents.itemTooltip(stack, components)
        }
    }

    private fun tabEvent() {
        CreativeModeTabEvents.MODIFY_OUTPUT_ALL.register { tab, output ->
            BuiltInRegistries.ITEM.keySet().filter { it.namespace == ModId }.forEach {
                val item = BuiltInRegistries.ITEM.getOptional(it).get()
                if (tab == CreativeTabRegistry.instance.blocks) {
                    if ((item is BlockItem || item is NamedBlockItem) && item.block !is NoTab)
                        output.accept(item)
                    return@forEach
                }

                if (BuiltInRegistries.BLOCK.getOptional(it).isPresent) return@forEach

                if (item is NoTab || tab != CreativeTabRegistry.instance.items) return@forEach

                if (item is HasSubItem) {
                    item.addSubItems(ItemStack(item)).forEach { stack ->
                        output.accept(stack)
                    }

                    return@forEach
                }

                output.accept(item)
            }
        }
    }

    private fun boundGemEvents() {
        UseItemCallback.EVENT.register evt@{ player, level, hand ->
            val stack = player.getItemInHand(hand)

            val item = stack.item
            if (item is BoundGem) {
                if (!player.isShiftKeyDown) return@evt InteractionResult.FAIL

                player.sendOverlayMessage(Component.translatable("tooltip.$ModId.bound_gem.revoke"))
                item.setBoundPos(stack, null)
            }

            InteractionResult.SUCCESS
        }

        UseBlockCallback.EVENT.register evt@{ player, level, hand, hit ->
            val stack = player.getItemInHand(hand)
            val pos = hit.blockPos

            val item = stack.item
            if (item is BoundGem) {
                val blockEntity = level.getBlockEntity(hit.blockPos)
                if (blockEntity !is MRUDevice || !blockEntity.holderType.isExporter || item.getBoundPos(stack) == null) return@evt InteractionResult.FAIL

                player.sendOverlayMessage(
                    Component.translatable("tooltip.$ModId.linked")
                        .append(": ")
                        .append("X: ${pos.x} Y: ${pos.y} Z: ${pos.z}")
                )

                if (stack.count > 1) {
                    val copied = stack.copy().apply {
                        this.count = 1
                        item.setBoundPos(this, pos)
                    }

                    stack.shrink(1)

                    val itemEntity = ItemEntity(level, player.x, player.y, player.z, copied).apply {
                        this.setNoPickUpDelay()
                        this.setThrower(player)
                    }

                    level.addFreshEntity(itemEntity)
                } else item.setBoundPos(stack, pos)
            }

            InteractionResult.SUCCESS
        }
    }

    private fun extendPlatform() {
        ResearchNetwork.sendToPlayer = { player, payload -> ServerPlayNetworking.send(player, payload) }
        ResearchNetwork.sendProgressToPlayer = { player, payload -> ServerPlayNetworking.send(player, payload) }

        countByIngredient = { (it.customIngredient as? CountIngredient)?.count ?: 1 }

        openMenuScreenInternal = menuScreen@{ player, provider, level, pos ->
            if (level.isClientSide) return@menuScreen
            val serverPlayer = player as ServerPlayer
            serverPlayer.openMenu(object : ExtendedMenuProvider<MenuTypeData> {
                override fun getDisplayName(): Component = provider.displayName

                override fun createMenu(
                    containerId: Int,
                    inventory: Inventory,
                    player: Player
                ): AbstractContainerMenu? = provider.createMenu(containerId, inventory, player)

                override fun getScreenOpeningData(player: ServerPlayer): MenuTypeData = MenuTypeData(pos)
            })
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T: Registry<*>> register(resourceKey: ResourceKey<T>, t: T): T =
        Registry.register(BuiltInRegistries.REGISTRY as Registry<Registry<*>>, resourceKey.identifier(), t)
}
