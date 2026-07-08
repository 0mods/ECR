package com.algorithmlx.ecr.common.init.registry

import com.algorithmlx.ecr.common.recipe.StructureRecipe
import com.algorithmlx.ecr.common.recipe.MithrilineFurnaceRecipe
import net.minecraft.world.item.crafting.RecipeType

interface RecipeTypeRegistry {
    val mithrilineFurnace: RecipeType<MithrilineFurnaceRecipe>
    val itemInStructure: RecipeType<StructureRecipe>

    companion object {
        lateinit var instance: RecipeTypeRegistry
    }
}
