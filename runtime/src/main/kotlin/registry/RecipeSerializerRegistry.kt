package com.algorithmlx.ecr.registry

import com.algorithmlx.ecr.common.recipe.MagicTableRecipe
import com.algorithmlx.ecr.common.recipe.StructureRecipe
import com.algorithmlx.ecr.common.recipe.MithrilineFurnaceRecipe
import net.minecraft.world.item.crafting.RecipeSerializer

interface RecipeSerializerRegistry {
    val mithrilineFurnace: RecipeSerializer<MithrilineFurnaceRecipe>
    val structure: RecipeSerializer<StructureRecipe>
    val magicTable: RecipeSerializer<MagicTableRecipe>

    companion object {
        @JvmStatic
        lateinit var instance: RecipeSerializerRegistry
    }
}
