package team._0mods.ecr.api.utils

import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeType

fun <T : Recipe<*>> simpleRecipeType(id: ResourceLocation) = object : RecipeType<T> {
    override fun toString(): String = id.toString()
}