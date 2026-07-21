package com.algorithmlx.ecr.registry

import com.algorithmlx.ecr.common.recipe.MithrilineFurnaceRecipe
import com.algorithmlx.ecr.common.recipe.StructureRecipe
import net.minecraft.world.item.crafting.display.RecipeDisplay

expect object RecipeDisplayTypeRegistry {
    val mithrilineFurnace: RecipeDisplay.Type<MithrilineFurnaceRecipe.Display>
    val structure: RecipeDisplay.Type<StructureRecipe.Display>
}
