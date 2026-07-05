package com.algorithmlx.ecr.common.init.registry

import com.algorithmlx.ecr.common.recipe.MithrilineFurnaceRecipe
import net.minecraft.world.item.crafting.RecipeSerializer

interface RecipeSerializerRegistry {
    val mithrilineFurnace: RecipeSerializer<MithrilineFurnaceRecipe>

    companion object {
        lateinit var instance: RecipeSerializerRegistry
    }
}
