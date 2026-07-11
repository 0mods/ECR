package com.algorithmlx.ecr.fabric.init

import com.algorithmlx.ecr.api.init.MultiblockMatcherTypes
import com.algorithmlx.ecr.api.menu.MenuTypeData
import com.algorithmlx.ecr.api.registries.ECRegistries
import com.algorithmlx.ecr.api.registries.ECRegistryKeys
import com.algorithmlx.ecr.api.utils.countByIngredient
import com.algorithmlx.ecr.api.utils.openMenuScreenInternal
import com.algorithmlx.ecr.common.init.registry.*
import com.algorithmlx.ecr.fabric.api.CountIngredient
import com.algorithmlx.ecr.fabric.init.registry.*
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuProvider
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceKey
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu

object ECRegistryInit {
    fun registrate() {
        register(ECRegistryKeys.MRU_TYPE_KEY, ECRegistries.MRU_TYPE)
        register(ECRegistryKeys.MULTIBLOCK_KEY, ECRegistries.MULTIBLOCK)
        register(ECRegistryKeys.BOOK_TYPE_KEY, ECRegistries.BOOK_TYPES)
        register(ECRegistryKeys.BOOK_ELEMENT_SERIALIZER_KEY, ECRegistries.BOOK_ELEMENT_SERIALIZER)
        register(ECRegistryKeys.RESEARCH_TASK_SERIALIZER_KEY, ECRegistries.RESEARCH_TASK_SERIALIZER)
        register(ECRegistryKeys.MULTIBLOCK_MATCHER_TYPE_KEY, ECRegistries.MULTIBLOCK_MATCHER_TYPE)

        FabricResearchSerializerRegistry.register()

        CustomIngredientSerializer.register(CountIngredient.SERIALIZER)

        DataComponentRegistry.instance = FabricDataComponentRegistry
        BlockCodecRegistry.instance = FabricBlockCodecRegistry
        BookLevelRegistry.instance = FabricBookLevelRegistry
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

        countByIngredient = { (it.customIngredient as? CountIngredient)?.count ?: 1 }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T: Registry<*>> register(resourceKey: ResourceKey<T>, t: T): T =
        Registry.register(BuiltInRegistries.REGISTRY as Registry<Registry<*>>, resourceKey.identifier(), t)
}
