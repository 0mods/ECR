package com.algorithmlx.ecr.fabric.init

import com.algorithmlx.ecr.api.init.MultiblockMatcherTypes
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
        register(ECRegistryKeys.BOOK_TYPE_KEY, ECRegistries.BOOK_TYPES)
        register(ECRegistryKeys.BOOK_ELEMENT_SERIALIZER_KEY, ECRegistries.BOOK_ELEMENT_SERIALIZER)
        register(ECRegistryKeys.RESEARCH_TASK_SERIALIZER_KEY, ECRegistries.RESEARCH_TASK_SERIALIZER)
        register(ECRegistryKeys.MULTIBLOCK_MATCHER_TYPE_KEY, ECRegistries.MULTIBLOCK_MATCHER_TYPE)

        FabricResearchSerializerRegistry.register()

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
        RecipeSerializerRegistry.instance = FabricRecipeSerializerRegistry
        RecipeTypeRegistry.instance = FabricRecipeTypeRegistry
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T: Registry<*>> register(resourceKey: ResourceKey<T>, t: T): T =
        Registry.register(BuiltInRegistries.REGISTRY as Registry<Registry<*>>, resourceKey.identifier(), t)
}
