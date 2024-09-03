package team._0mods.ecr.common.compact.jei.categories

import com.mojang.blaze3d.vertex.PoseStack
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
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import team._0mods.ecr.ModId
import team._0mods.ecr.api.utils.rl
import team._0mods.ecr.common.compact.jei.ECJEIPlugin
import team._0mods.ecr.common.init.registry.ECRegistry
import team._0mods.ecr.common.recipes.MithrilineFurnaceRecipe

class MithrilineFurnaceCategory(guiHelper: IGuiHelper): IRecipeCategory<MithrilineFurnaceRecipe> {
    companion object {
        @JvmField
        val RL_ID = "$ModId:mithriline_furnace".rl
    }

    private val icon = guiHelper.createDrawableItemStack(ItemStack(ECRegistry.mithrilineFurnace.get()))
    private val bg = guiHelper.createDrawable("$ModId:textures/gui/jei/${RL_ID.path}.png".rl, 0, 0, 170, 80)

    override fun getRecipeType(): RecipeType<MithrilineFurnaceRecipe> = ECJEIPlugin.MITHRILINE_FURNACE

    override fun getTitle(): Component = ECRegistry.mithrilineFurnace.get().name.withStyle(ChatFormatting.RESET)

    override fun getBackground(): IDrawable = bg

    override fun getIcon(): IDrawable = icon

    override fun setRecipe(builder: IRecipeLayoutBuilder, recipe: MithrilineFurnaceRecipe, focuses: IFocusGroup) {
        builder.addSlot(RecipeIngredientRole.INPUT, 77, 8).addIngredients(recipe.ingredients[0])

        builder.addSlot(RecipeIngredientRole.OUTPUT, 77, 46).addItemStack(recipe.resultItem)
    }

    override fun draw(
        recipe: MithrilineFurnaceRecipe,
        recipeSlotsView: IRecipeSlotsView,
        stack: PoseStack,
        mouseX: Double,
        mouseY: Double
    ) {
        val font = Minecraft.getInstance().font
        val text = Component.literal("${recipe.espe} ESPE")
        font.draw(stack, text, 85 - font.width(text) / 2f, 68f, 0x000000)
    }
}