package com.algorithmlx.ecr.common.init.registry

import com.algorithmlx.ecr.common.recipe.StructureRecipe
import com.algorithmlx.ecr.common.recipe.MithrilineFurnaceRecipe
import net.minecraft.world.item.crafting.RecipeSerializer

interface RecipeSerializerRegistry {
    val mithrilineFurnace: RecipeSerializer<MithrilineFurnaceRecipe>
    val itemInStructure: RecipeSerializer<StructureRecipe>

    companion object {
        lateinit var instance: RecipeSerializerRegistry
    }
}
