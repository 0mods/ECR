package com.algorithmlx.ecr.neoforge.init.registry

import com.algorithmlx.ecr.api.ModId
import com.algorithmlx.ecr.common.init.ECRModIDs
import com.algorithmlx.ecr.common.init.registry.RecipeSerializerRegistry
import com.algorithmlx.ecr.common.recipe.EnvoyerRecipe
import com.algorithmlx.ecr.common.recipe.StructureRecipe
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
    private val structureRecipe = recipeSerializers.register(ECRModIDs.STRUCTURE) { _ ->
        RecipeSerializer(StructureRecipe.CODEC, StructureRecipe.STREAM_CODEC)
    }
    private val envoyerRecipe = recipeSerializers.register(ECRModIDs.ENVOYER) { _ ->
        RecipeSerializer(EnvoyerRecipe.CODEC, EnvoyerRecipe.STREAM_CODEC)
    }

    override val mithrilineFurnace: RecipeSerializer<MithrilineFurnaceRecipe> by lazy { mithrilineFurnaceSerializer.get() }
    override val structure: RecipeSerializer<StructureRecipe> by lazy { structureRecipe.get() }
    override val envoyer: RecipeSerializer<EnvoyerRecipe> by lazy { envoyerRecipe.get() }
}
