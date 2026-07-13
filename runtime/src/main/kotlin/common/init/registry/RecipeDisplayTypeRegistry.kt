package com.algorithmlx.ecr.common.init.registry

import com.algorithmlx.ecr.common.recipe.MithrilineFurnaceRecipe
import com.algorithmlx.ecr.common.recipe.StructureRecipe
import net.minecraft.world.item.crafting.display.RecipeDisplay

interface RecipeDisplayTypeRegistry {
    val mithrilineFurnace: RecipeDisplay.Type<MithrilineFurnaceRecipe.Display>
    val structure: RecipeDisplay.Type<StructureRecipe.Display>

    companion object {
        lateinit var instance: RecipeDisplayTypeRegistry
    }
}
