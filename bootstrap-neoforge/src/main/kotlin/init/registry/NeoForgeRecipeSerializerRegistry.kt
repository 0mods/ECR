package com.algorithmlx.ecr.neoforge.init.registry

import com.algorithmlx.ecr.api.ModId
import com.algorithmlx.ecr.common.init.ECRModIDs
import com.algorithmlx.ecr.common.init.registry.RecipeSerializerRegistry
import com.algorithmlx.ecr.common.recipe.ItemInStructureRecipe
import com.algorithmlx.ecr.common.recipe.MithrilineFurnaceRecipe
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.crafting.RecipeSerializer
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister

class NeoForgeRecipeSerializerRegistry(bus: IEventBus): RecipeSerializerRegistry {
    private val recipeSerializers = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, ModId)

    init {
        recipeSerializers.register(bus)
    }

    private val mithrilineFurnaceSerializer = recipeSerializers.register(ECRModIDs.MITHRILINE_FURNACE) { _ ->
        RecipeSerializer(MithrilineFurnaceRecipe.CODEC, MithrilineFurnaceRecipe.STREAM_CODEC)
    }
    private val itemInStructureRecipe = recipeSerializers.register(ECRModIDs.ITEM_IN_STRUCTURE) { _ ->
        RecipeSerializer(ItemInStructureRecipe.CODEC, ItemInStructureRecipe.STREAM_CODEC)
    }

    override val mithrilineFurnace: RecipeSerializer<MithrilineFurnaceRecipe> by lazy { mithrilineFurnaceSerializer.get() }
    override val itemInStructure: RecipeSerializer<ItemInStructureRecipe> by lazy { itemInStructureRecipe.get() }
}
