package com.algorithmlx.ecr.fabric.init.registry

import com.algorithmlx.ecr.api.ecRL
import com.algorithmlx.ecr.common.init.registry.RecipeTypeRegistry
import com.algorithmlx.ecr.common.recipe.MithrilineFurnaceRecipe
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeType

object FabricRecipeTypeRegistry: RecipeTypeRegistry {
    override val mithrilineFurnace: RecipeType<MithrilineFurnaceRecipe> = registerSimple("mithriline_furnace")

    private fun <T: Recipe<*>> registerSimple(id: String): RecipeType<T> {
        val name = id.ecRL.toString()
        val rt = object: RecipeType<T> {
            override fun toString(): String = name
        }

        return register(id, rt)
    }

    private fun <T: RecipeType<*>> register(id: String, recipe: T): T = Registry.register(
        BuiltInRegistries.RECIPE_TYPE, id.ecRL, recipe
    )
}
