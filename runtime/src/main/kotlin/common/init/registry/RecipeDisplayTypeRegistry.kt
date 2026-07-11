package com.algorithmlx.ecr.common.init.registry

import com.algorithmlx.ecr.common.recipe.MithrilineFurnaceRecipe
import net.minecraft.world.item.crafting.display.RecipeDisplay

interface RecipeDisplayTypeRegistry {
    val mithrilineFurnace: RecipeDisplay.Type<MithrilineFurnaceRecipe.Display>

    companion object {
        lateinit var instance: RecipeDisplayTypeRegistry
    }
}
