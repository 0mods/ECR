package com.algorithmlx.ecr.common.init.registry

import com.algorithmlx.ecr.common.recipe.ItemInStructureRecipe
import com.algorithmlx.ecr.common.recipe.MithrilineFurnaceRecipe
import net.minecraft.world.item.crafting.RecipeSerializer

interface RecipeSerializerRegistry {
    val mithrilineFurnace: RecipeSerializer<MithrilineFurnaceRecipe>
    val itemInStructure: RecipeSerializer<ItemInStructureRecipe>

    companion object {
        lateinit var instance: RecipeSerializerRegistry
    }
}
