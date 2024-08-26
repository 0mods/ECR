package team._0mods.ecr.common.compact.jei

import mezz.jei.api.IModPlugin
import mezz.jei.api.JeiPlugin
import mezz.jei.api.recipe.RecipeType
import mezz.jei.api.registration.*
import net.minecraft.client.Minecraft
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import team._0mods.ecr.ModId
import team._0mods.ecr.api.utils.rl
import team._0mods.ecr.client.screen.container.MithrilineFurnaceScreen
import team._0mods.ecr.common.compact.jei.categories.MithrilineFurnaceCategory
import team._0mods.ecr.common.container.MithrilineFurnaceContainer
import team._0mods.ecr.common.init.registry.ECRegistry
import team._0mods.ecr.common.recipes.MithrilineFurnaceRecipe

@JeiPlugin
class ECJEIPlugin: IModPlugin {
    companion object {
        @JvmField val MITHRILINE_FURNACE = RecipeType(MithrilineFurnaceCategory.RL_ID, MithrilineFurnaceRecipe::class.java)
    }

    override fun getPluginUid(): ResourceLocation = "$ModId:jei_plugin".rl

    override fun registerCategories(registration: IRecipeCategoryRegistration) {
        registration.addRecipeCategories(
            MithrilineFurnaceCategory(registration.jeiHelpers.guiHelper)
        )
    }

    override fun registerRecipeCatalysts(registration: IRecipeCatalystRegistration) {
        registration.addRecipeCatalyst(ItemStack(ECRegistry.mithrilineFurnace.first), MITHRILINE_FURNACE)
    }

    override fun registerRecipes(registration: IRecipeRegistration) {
        val mgr = Minecraft.getInstance().level?.recipeManager ?: return

        val allMithriline = mgr.getAllRecipesFor(ECRegistry.mithrilineFurnaceRecipe.get())
        registration.addRecipes(MITHRILINE_FURNACE, allMithriline)
    }

    override fun registerGuiHandlers(registration: IGuiHandlerRegistration) {
        registration.addRecipeClickArea(MithrilineFurnaceScreen::class.java, 83, 40, 9, 18, MITHRILINE_FURNACE)
    }

    override fun registerRecipeTransferHandlers(registration: IRecipeTransferRegistration) {
        registration.addRecipeTransferHandler(
            MithrilineFurnaceContainer::class.java,
            ECRegistry.mithrilineFurnaceContainer.get(),
            MITHRILINE_FURNACE,
            0,
            1,
            3,
            36
        )
    }
}
