package com.algorithmlx.ecr.common.init

import com.algorithmlx.ecr.common.recipe.MithrilineFurnaceRecipe
import net.minecraft.world.item.crafting.RecipeSerializer

interface RecipeSerializerRegistry {
    val mithrilineRecipeSerializer: RecipeSerializer<MithrilineFurnaceRecipe>

    companion object {
        @JvmStatic
        val instance: RecipeSerializerRegistry = UnionRegistry.instance
    }
}
