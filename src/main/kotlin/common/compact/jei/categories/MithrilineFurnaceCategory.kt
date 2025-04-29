package team._0mods.ecr.common.compact.jei.categories

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder
import mezz.jei.api.gui.drawable.IDrawable
import mezz.jei.api.gui.ingredient.IRecipeSlotsView
import mezz.jei.api.helpers.IGuiHelper
import mezz.jei.api.recipe.IFocusGroup
import mezz.jei.api.recipe.RecipeIngredientRole
import mezz.jei.api.recipe.RecipeType
import mezz.jei.api.recipe.category.IRecipeCategory
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import team._0mods.ecr.api.utils.ecRL
import team._0mods.ecr.common.compact.jei.ECRJEIPlugin
import team._0mods.ecr.common.init.registry.ECRegistry
import team._0mods.ecr.common.recipes.MithrilineFurnaceRecipe
import java.awt.Color

class MithrilineFurnaceCategory(guiHelper: IGuiHelper): IRecipeCategory<MithrilineFurnaceRecipe> {
    private val icon = guiHelper.createDrawableItemStack(ItemStack(ECRegistry.mithrilineFurnace))
    private val bg = guiHelper.createDrawable("textures/gui/jei/${RL_ID.path}.png".ecRL, 0, 0, 170, 80)

    override fun getRecipeType(): RecipeType<MithrilineFurnaceRecipe> = ECRJEIPlugin.MITHRILINE_FURNACE

    override fun getTitle(): Component = ECRegistry.mithrilineFurnace.name.withStyle(ChatFormatting.RESET)

    override fun getWidth(): Int = bg.width

    override fun getHeight(): Int = bg.height

    override fun getIcon(): IDrawable = icon

    override fun setRecipe(builder: IRecipeLayoutBuilder, recipe: MithrilineFurnaceRecipe, focuses: IFocusGroup) {
        builder.addSlot(RecipeIngredientRole.INPUT, 77, 8).addIngredients(recipe.ingredients[0])

        builder.addSlot(RecipeIngredientRole.OUTPUT, 77, 46).addItemStack(recipe.result)
    }

    override fun draw(
        recipe: MithrilineFurnaceRecipe,
        recipeSlotsView: IRecipeSlotsView,
        guiGraphics: GuiGraphics,
        mouseX: Double,
        mouseY: Double
    ) {
        bg.draw(guiGraphics)

        val font = Minecraft.getInstance().font
        val text = Component.literal("${recipe.espe} ESPE")
        guiGraphics.drawString(font, text, 85 - font.width(text) / 2, 68, Color.WHITE.rgb)
        super.draw(recipe, recipeSlotsView, guiGraphics, mouseX, mouseY)
    }

    companion object {
        @JvmField
        val RL_ID = "mithriline_furnace".ecRL
    }
}
