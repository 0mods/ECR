package team._0mods.ecr.common.compact

import mezz.jei.api.IModPlugin
import mezz.jei.api.JeiPlugin
import mezz.jei.api.recipe.RecipeType
import mezz.jei.api.registration.IRecipeCatalystRegistration
import mezz.jei.api.registration.IRecipeCategoryRegistration
import mezz.jei.api.registration.IRecipeRegistration
import net.minecraft.client.Minecraft
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import team._0mods.ecr.ModId
import team._0mods.ecr.api.utils.rl
import team._0mods.ecr.common.compact.categories.MithrilineFurnaceCategory
import team._0mods.ecr.common.init.registry.ECRegistry
import team._0mods.ecr.common.recipes.MithrilineFurnaceRecipe

@JeiPlugin
class ECJEIPlugin: IModPlugin {
    companion object {
        @JvmField
        val MITHRILINE_FURNACE = RecipeType(MithrilineFurnaceCategory.RL_ID, MithrilineFurnaceRecipe::class.java)
    }

    override fun getPluginUid(): ResourceLocation = "$ModId:jei_plugin".rl

    override fun registerCategories(registration: IRecipeCategoryRegistration) {
        registration.addRecipeCategories(
            MithrilineFurnaceCategory(registration.jeiHelpers.guiHelper)
        )
    }

    override fun registerRecipeCatalysts(registration: IRecipeCatalystRegistration) {
        registration.addRecipeCatalyst(ItemStack(ECRegistry.mithrilineFurnace.first), MITHRILINE_FURNACE)
        registration.addRecipeCatalyst(ItemStack(ECRegistry.mithrilineCrystal.get()), MITHRILINE_FURNACE)
    }

    override fun registerRecipes(registration: IRecipeRegistration) {
        val mgr = Minecraft.getInstance().level?.recipeManager ?: return

        val allMithriline = mgr.getAllRecipesFor(ECRegistry.mithrilineFurnaceRecipe.get())
        registration.addRecipes(MITHRILINE_FURNACE, allMithriline)
    }
}