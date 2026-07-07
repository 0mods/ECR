package com.algorithmlx.ecr.client.book

import com.algorithmlx.ecr.api.client.research.BookRecipeViewers
import net.minecraft.world.item.ItemStack

object RecipeViewerIntegrations {
    private var initialized = false

    fun init() {
        if (initialized) return
        initialized = true
        BookRecipeViewers.register(::openEmi)
        BookRecipeViewers.register(::openJei)
        BookRecipeViewers.register(::openRei)
    }

    private fun openEmi(stack: ItemStack): Boolean = runCatching {
        val emiStackClass = Class.forName("dev.emi.emi.api.stack.EmiStack")
        val ingredientClass = Class.forName("dev.emi.emi.api.stack.EmiIngredient")
        val ingredient = emiStackClass.getMethod("of", ItemStack::class.java).invoke(null, stack)
        Class.forName("dev.emi.emi.api.EmiApi")
            .getMethod("displayRecipes", ingredientClass)
            .invoke(null, ingredient)
        true
    }.getOrDefault(false)

    private fun openJei(stack: ItemStack): Boolean = runCatching {
        val runtimeClass = Class.forName("mezz.jei.api.runtime.IJeiRuntime")
        val runtime = Class.forName("mezz.jei.common.Internal").getMethod("getJeiRuntime").invoke(null)
        val helpers = runtimeClass.getMethod("getJeiHelpers").invoke(runtime)
        val helpersClass = Class.forName("mezz.jei.api.helpers.IJeiHelpers")
        val focusFactory = helpersClass.getMethod("getFocusFactory").invoke(helpers)
        val roleClass = Class.forName("mezz.jei.api.recipe.RecipeIngredientRole")
        val outputRole = roleClass.enumConstants.first { (it as Enum<*>).name == "OUTPUT" }
        val ingredientTypeClass = Class.forName("mezz.jei.api.ingredients.IIngredientType")
        val itemType = Class.forName("mezz.jei.api.constants.VanillaTypes").getField("ITEM_STACK").get(null)
        val focus = Class.forName("mezz.jei.api.recipe.IFocusFactory")
            .getMethod("createFocus", roleClass, ingredientTypeClass, Any::class.java)
            .invoke(focusFactory, outputRole, itemType, stack)
        val recipesGui = runtimeClass.getMethod("getRecipesGui").invoke(runtime)
        Class.forName("mezz.jei.api.runtime.IRecipesGui")
            .getMethod("show", Class.forName("mezz.jei.api.recipe.IFocus"))
            .invoke(recipesGui, focus)
        true
    }.getOrDefault(false)

    private fun openRei(stack: ItemStack): Boolean = runCatching {
        val entryStackClass = Class.forName("me.shedaniel.rei.api.common.entry.EntryStack")
        val entry = Class.forName("me.shedaniel.rei.api.common.util.EntryStacks")
            .getMethod("of", ItemStack::class.java)
            .invoke(null, stack)
        val builderClass = Class.forName("me.shedaniel.rei.api.client.view.ViewSearchBuilder")
        val builder = builderClass.getMethod("builder").invoke(null)
        builderClass.getMethod("addRecipesFor", entryStackClass).invoke(builder, entry)
        builderClass.getMethod("open").invoke(builder) as Boolean
    }.getOrDefault(false)
}
