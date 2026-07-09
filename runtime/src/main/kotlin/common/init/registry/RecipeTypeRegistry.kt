package com.algorithmlx.ecr.common.init.registry

import com.algorithmlx.ecr.common.recipe.EnvoyerRecipe
import com.algorithmlx.ecr.common.recipe.StructureRecipe
import com.algorithmlx.ecr.common.recipe.MithrilineFurnaceRecipe
import net.minecraft.world.item.crafting.RecipeType

interface RecipeTypeRegistry {
    val mithrilineFurnace: RecipeType<MithrilineFurnaceRecipe>
    val structure: RecipeType<StructureRecipe>
    val envoyer: RecipeType<EnvoyerRecipe>

    companion object {
        lateinit var instance: RecipeTypeRegistry
    }
}
