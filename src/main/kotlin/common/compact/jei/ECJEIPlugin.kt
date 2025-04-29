package team._0mods.ecr.common.compact.jei

import mezz.jei.api.*
import mezz.jei.api.recipe.RecipeType
import mezz.jei.api.registration.*
import net.minecraft.client.Minecraft
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import team._0mods.ecr.api.utils.ecRL
import team._0mods.ecr.client.screen.menu.MithrilineFurnaceScreen
import team._0mods.ecr.common.compact.jei.categories.*
import team._0mods.ecr.common.init.registry.ECRegistry
import team._0mods.ecr.common.menu.MithrilineFurnaceMenu
import team._0mods.ecr.common.recipes.*

@JeiPlugin
class ECJEIPlugin: IModPlugin {
    companion object {
        @JvmField val MITHRILINE_FURNACE = RecipeType(MithrilineFurnaceCategory.RL_ID, MithrilineFurnaceRecipe::class.java)
        @JvmField val ENVOYER = RecipeType(EnvoyerCategory.RL_ID, XLikeRecipe.Envoyer::class.java)
        @JvmField val MAGIC_TABLE = RecipeType(MagicTableCategory.RL_ID, XLikeRecipe.MagicTable::class.java)
    }

    override fun getPluginUid(): ResourceLocation = "jei_plugin".ecRL

    override fun registerCategories(registration: IRecipeCategoryRegistration) {
        val gh = registration.jeiHelpers.guiHelper

        registration.addRecipeCategories(
            MithrilineFurnaceCategory(gh),
            EnvoyerCategory(gh),
            MagicTableCategory(gh)
        )
    }

    override fun registerRecipeCatalysts(registration: IRecipeCatalystRegistration) {
        registration.addRecipeCatalyst(ItemStack(ECRegistry.mithrilineFurnace), MITHRILINE_FURNACE)
        registration.addRecipeCatalyst(ItemStack(ECRegistry.envoyer), ENVOYER)
        registration.addRecipeCatalyst(ItemStack(ECRegistry.magicTable), MAGIC_TABLE)
    }

    override fun registerRecipes(registration: IRecipeRegistration) {
        val mgr = Minecraft.getInstance().level?.recipeManager ?: return

        registration.addRecipes(MITHRILINE_FURNACE, mgr.getAllRecipesFor(ECRegistry.mithrilineFurnaceRecipe))
        registration.addRecipes(ENVOYER, mgr.getAllRecipesFor(ECRegistry.envoyerRecipe))
        registration.addRecipes(MAGIC_TABLE, mgr.getAllRecipesFor(ECRegistry.magicTableRecipe))
    }

    override fun registerGuiHandlers(registration: IGuiHandlerRegistration) {
        registration.addRecipeClickArea(MithrilineFurnaceScreen::class.java, 83, 40, 9, 18, MITHRILINE_FURNACE)
    }

    override fun registerRecipeTransferHandlers(registration: IRecipeTransferRegistration) {
        registration.addRecipeTransferHandler(
            MithrilineFurnaceMenu::class.java,
            ECRegistry.mithrilineFurnaceMenu,
            MITHRILINE_FURNACE,
            0, 1, 2, 36
        )
    }
}
