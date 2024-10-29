package team._0mods.ecr.common.compact.jei

import mezz.jei.api.*
import mezz.jei.api.recipe.RecipeType
import mezz.jei.api.registration.*
import net.minecraft.client.Minecraft
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import ru.hollowhorizon.hc.client.utils.rl
import team._0mods.ecr.api.ModId
import team._0mods.ecr.client.screen.container.MithrilineFurnaceScreen
import team._0mods.ecr.common.compact.jei.categories.*
import team._0mods.ecr.common.container.MithrilineFurnaceContainer
import team._0mods.ecr.common.init.registry.ECRegistry
import team._0mods.ecr.common.recipes.*

@JeiPlugin
class ECJEIPlugin: IModPlugin {
    companion object {
        @JvmField val MITHRILINE_FURNACE = RecipeType(MithrilineFurnaceCategory.RL_ID, MithrilineFurnaceRecipe::class.java)
        @JvmField val ENVOYER = RecipeType(EnvoyerCategory.RL_ID, EnvoyerRecipe::class.java)
    }

    override fun getPluginUid(): ResourceLocation = "$ModId:jei_plugin".rl

    override fun registerCategories(registration: IRecipeCategoryRegistration) {
        val gh = registration.jeiHelpers.guiHelper

        registration.addRecipeCategories(
            MithrilineFurnaceCategory(gh),
            EnvoyerCategory(gh)
        )
    }

    override fun registerRecipeCatalysts(registration: IRecipeCatalystRegistration) {
        registration.addRecipeCatalyst(ItemStack(ECRegistry.mithrilineFurnace.get()), MITHRILINE_FURNACE)
        registration.addRecipeCatalyst(ItemStack(ECRegistry.envoyer.get()), ENVOYER)
    }

    override fun registerRecipes(registration: IRecipeRegistration) {
        val mgr = Minecraft.getInstance().level?.recipeManager ?: return

        registration.addRecipes(MITHRILINE_FURNACE, mgr.getAllRecipesFor(ECRegistry.mithrilineFurnaceRecipe.get()))
        registration.addRecipes(ENVOYER, mgr.getAllRecipesFor(ECRegistry.envoyerRecipe.get()))
    }

    override fun registerGuiHandlers(registration: IGuiHandlerRegistration) {
        registration.addRecipeClickArea(MithrilineFurnaceScreen::class.java, 83, 40, 9, 18, MITHRILINE_FURNACE)
    }

    override fun registerRecipeTransferHandlers(registration: IRecipeTransferRegistration) {
        registration.addRecipeTransferHandler(
            MithrilineFurnaceContainer::class.java,
            ECRegistry.mithrilineFurnaceContainer.get(),
            MITHRILINE_FURNACE, 0, 1, 2, 36
        )
    }
}
