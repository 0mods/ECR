package com.algorithmlx.ecr.registry

import com.algorithmlx.ecr.common.recipe.MagicTableRecipe
import com.algorithmlx.ecr.common.recipe.MithrilineFurnaceRecipe
import com.algorithmlx.ecr.common.recipe.RadiatingChamberRecipe
import com.algorithmlx.ecr.common.recipe.StructureRecipe
import net.minecraft.world.item.crafting.display.RecipeDisplay

interface RecipeDisplayTypeRegistry {
    val mithrilineFurnace: RecipeDisplay.Type<MithrilineFurnaceRecipe.Display>
    val radiatingChamber: RecipeDisplay.Type<RadiatingChamberRecipe.Display>
    val structure: RecipeDisplay.Type<StructureRecipe.Display>
    val magicTable: RecipeDisplay.Type<MagicTableRecipe.Display>

    companion object {
        @JvmStatic
        lateinit var instance: RecipeDisplayTypeRegistry
    }
}
