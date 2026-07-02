package com.algorithmlx.ecr.api.utils

import net.minecraft.resources.Identifier
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeType

fun <T : Recipe<*>> simpleRecipeType(id: Identifier) = object : RecipeType<T> {
    override fun toString(): String = id.toString()
}
