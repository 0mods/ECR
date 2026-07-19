package com.algorithmlx.ecr.common.init.registry

import com.algorithmlx.ecr.common.recipe.MagicTableRecipe
import com.algorithmlx.ecr.common.recipe.StructureRecipe
import com.algorithmlx.ecr.common.recipe.MithrilineFurnaceRecipe
import net.minecraft.world.item.crafting.RecipeSerializer

interface RecipeSerializerRegistry {
    val mithrilineFurnace: RecipeSerializer<MithrilineFurnaceRecipe>
    val structure: RecipeSerializer<StructureRecipe>
    val envoyer: RecipeSerializer<MagicTableRecipe>

    companion object {
        lateinit var instance: RecipeSerializerRegistry
    }
}
