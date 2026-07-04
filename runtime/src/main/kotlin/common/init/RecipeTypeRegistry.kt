package com.algorithmlx.ecr.common.init

import com.algorithmlx.ecr.common.recipe.MithrilineFurnaceRecipe
import net.minecraft.world.item.crafting.RecipeType

interface RecipeTypeRegistry {
    val mithrilineRecipeType: RecipeType<MithrilineFurnaceRecipe>

    companion object {
        @JvmStatic
        val instance: RecipeTypeRegistry = UnionRegistry.instance
    }
}
