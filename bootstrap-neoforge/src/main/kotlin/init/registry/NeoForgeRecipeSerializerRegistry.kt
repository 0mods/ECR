package com.algorithmlx.ecr.neoforge.init.registry

import com.algorithmlx.ecr.api.ModId
import com.algorithmlx.ecr.common.init.ECRModIDs
import com.algorithmlx.ecr.common.recipe.*
import com.algorithmlx.ecr.registry.RecipeSerializerRegistry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeSerializer
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister

class NeoForgeRecipeSerializerRegistry(bus: IEventBus): RecipeSerializerRegistry {
    private val recipeSerializers = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, ModId)

    init {
        recipeSerializers.register(bus)
    }

    override val mithrilineFurnace: RecipeSerializer<MithrilineFurnaceRecipe> by register(ECRModIDs.MITHRILINE_FURNACE) { RecipeSerializer(MithrilineFurnaceRecipe.CODEC, MithrilineFurnaceRecipe.STREAM_CODEC) }
    override val structure: RecipeSerializer<StructureRecipe> by register(ECRModIDs.STRUCTURE) { RecipeSerializer(StructureRecipe.CODEC, StructureRecipe.STREAM_CODEC) }
    override val magicTable: RecipeSerializer<MagicTableRecipe> by register(ECRModIDs.MAGIC_TABLE) { RecipeSerializer(MagicTableRecipe.CODEC, MagicTableRecipe.STREAM_CODEC) }

    private fun <T: Recipe<*>> register(id: String, factory: () -> RecipeSerializer<T>) = recipeSerializers.register(id) { _ -> factory() }
}
