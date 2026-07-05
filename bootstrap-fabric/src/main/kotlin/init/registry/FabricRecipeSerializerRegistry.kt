package com.algorithmlx.ecr.fabric.init.registry

import com.algorithmlx.ecr.api.ecRL
import com.algorithmlx.ecr.common.init.registry.RecipeSerializerRegistry
import com.algorithmlx.ecr.common.recipe.MithrilineFurnaceRecipe
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.crafting.RecipeSerializer

object FabricRecipeSerializerRegistry: RecipeSerializerRegistry {
    override val mithrilineFurnace: RecipeSerializer<MithrilineFurnaceRecipe> = register(
        "mithriline_furnace", RecipeSerializer(MithrilineFurnaceRecipe.codec, MithrilineFurnaceRecipe.streamCodec)
    )

    private fun <T: RecipeSerializer<*>> register(id: String, recipeSerializer: T): T = Registry.register(
        BuiltInRegistries.RECIPE_SERIALIZER, id.ecRL, recipeSerializer
    )
}
