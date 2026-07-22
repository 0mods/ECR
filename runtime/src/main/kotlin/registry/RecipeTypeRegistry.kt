package com.algorithmlx.ecr.registry

import com.algorithmlx.ecr.common.recipe.MagicTableRecipe
import com.algorithmlx.ecr.common.recipe.StructureRecipe
import com.algorithmlx.ecr.common.recipe.MithrilineFurnaceRecipe
import net.minecraft.world.item.crafting.RecipeType

interface RecipeTypeRegistry {
    val mithrilineFurnace: RecipeType<MithrilineFurnaceRecipe>
    val structure: RecipeType<StructureRecipe>
    val magicTable: RecipeType<MagicTableRecipe>

    companion object {
        @JvmStatic
        lateinit var instance: RecipeTypeRegistry
    }
}
