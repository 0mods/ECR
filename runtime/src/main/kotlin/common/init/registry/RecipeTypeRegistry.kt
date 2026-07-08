package com.algorithmlx.ecr.common.init.registry

import com.algorithmlx.ecr.common.recipe.ItemInStructureRecipe
import com.algorithmlx.ecr.common.recipe.MithrilineFurnaceRecipe
import net.minecraft.world.item.crafting.RecipeType

interface RecipeTypeRegistry {
    val mithrilineFurnace: RecipeType<MithrilineFurnaceRecipe>
    val itemInStructure: RecipeType<ItemInStructureRecipe>

    companion object {
        lateinit var instance: RecipeTypeRegistry
    }
}
