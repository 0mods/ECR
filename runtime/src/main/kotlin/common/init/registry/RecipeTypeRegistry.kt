package com.algorithmlx.ecr.common.init.registry

import com.algorithmlx.ecr.common.recipe.MithrilineFurnaceRecipe
import net.minecraft.world.item.crafting.RecipeType

interface RecipeTypeRegistry {
    val mithrilineFurnace: RecipeType<MithrilineFurnaceRecipe>

    companion object {
        lateinit var instance: RecipeTypeRegistry
    }
}
