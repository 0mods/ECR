package com.algorithmlx.ecr.fabric.init

import com.algorithmlx.ecr.api.registries.ECRegistries
import com.algorithmlx.ecr.api.registries.ECRegistryKeys
import com.algorithmlx.ecr.common.init.registry.*
import com.algorithmlx.ecr.fabric.init.registry.*
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceKey

object ECRegistryInit {
    fun registrate() {
        register(ECRegistryKeys.MRU_TYPE_KEY, ECRegistries.MRU_TYPE)
        register(ECRegistryKeys.MULTIBLOCK_KEY, ECRegistries.MULTIBLOCK)
        register(ECRegistryKeys.BOOK_LEVEL_KEY, ECRegistries.BOOK_LEVEL)

        BlockCodecRegistry.instance = FabricBlockCodecRegistry
        BookLevelRegistry.instance = FabricBookLevelRegistry
        BlockRegistry.instance = FabricBlockRegistry
        BlockEntityTypeRegistry.instance = FabricBlockEntityTypeRegistry
        DataComponentRegistry.instance = FabricDataComponentRegistry
        ItemRegistry.instance = FabricItemRegistry
        MenuTypeRegistry.instance = FabricMenuTypeRegistry
        MRUTypeRegistry.instance = FabricMRUTypeRegistry
        MultiblockRegistry.instance = FabricMultiblockRegistry
        RecipeSerializerRegistry.instance = FabricRecipeSerializerRegistry
        RecipeTypeRegistry.instance = FabricRecipeTypeRegistry
    }

    private fun <T: Registry<*>> register(resourceKey: ResourceKey<T>, t: T): T =
        Registry.register(BuiltInRegistries.REGISTRY as Registry<Registry<*>>, resourceKey.identifier(), t)
}
