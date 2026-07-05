package com.algorithmlx.ecr.neoforge.init.registry

import com.algorithmlx.ecr.api.ModId
import com.algorithmlx.ecr.common.init.registry.RecipeSerializerRegistry
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

    private val mithrilineFurnaceSerializer = recipeSerializers.register("mithriline_furnace") { _ ->
        RecipeSerializer(MithrilineFurnaceRecipe.codec, MithrilineFurnaceRecipe.streamCodec)
    }

    override val mithrilineFurnace: RecipeSerializer<MithrilineFurnaceRecipe> by lazy { mithrilineFurnaceSerializer.get() }
}
