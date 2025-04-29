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
import ru.hollowhorizon.hc.common.utils.mcTranslate
import team._0mods.ecr.api.ModId
import team._0mods.ecr.api.client.isCursorAtPos
import team._0mods.ecr.api.utils.ecRL
import team._0mods.ecr.common.compact.jei.ECJEIPlugin
import team._0mods.ecr.common.init.registry.ECRegistry
import team._0mods.ecr.common.recipes.XLikeRecipe
import java.awt.Color

class EnvoyerCategory(guiHelper: IGuiHelper): IRecipeCategory<XLikeRecipe.Envoyer> {
    private val icon = guiHelper.createDrawableItemStack(ItemStack(ECRegistry.envoyer))
    private val bg = guiHelper.drawableBuilder("textures/gui/jei/x.png".ecRL, 0, 0, 126, 54)
        .setTextureSize(126, 54).build()

    override fun getRecipeType(): RecipeType<XLikeRecipe.Envoyer> = ECJEIPlugin.ENVOYER

    override fun getTitle(): Component = ECRegistry.envoyer.name.withStyle(ChatFormatting.RESET)

    override fun getIcon(): IDrawable? = icon

    override fun getWidth(): Int = bg.width

    override fun getHeight(): Int = bg.height

    override fun setRecipe(builder: IRecipeLayoutBuilder, recipe: XLikeRecipe.Envoyer, focuses: IFocusGroup) {
        val level = Minecraft.getInstance().level ?: return

        val inp = recipe.ingredients

        builder.addSlot(RecipeIngredientRole.INPUT, 1, 1).addIngredients(inp[0])
        builder.addSlot(RecipeIngredientRole.INPUT, 37, 1).addIngredients(inp[1])
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 37).addIngredients(inp[2])
        builder.addSlot(RecipeIngredientRole.INPUT, 37, 37).addIngredients(inp[3])

        builder.addSlot(RecipeIngredientRole.CATALYST, 19, 19).addIngredients(inp[4])

        builder.addSlot(RecipeIngredientRole.OUTPUT, 91, 19).addItemStack(recipe.getResultItem(level.registryAccess()))
    }

    override fun draw(
        recipe: XLikeRecipe.Envoyer,
        recipeSlotsView: IRecipeSlotsView,
        guiGraphics: GuiGraphics,
        mouseX: Double,
        mouseY: Double
    ) {
        bg.draw(guiGraphics)
        val x = 73
        val y = 1
        guiGraphics.fillGradient(x, y, x + 52, y + 8, Color(139, 0, 255).rgb, Color(50, 18, 122).rgb)

        if (isCursorAtPos(mouseX, mouseY, x, y, 52, 8))
            guiGraphics.renderTooltip(
                Minecraft.getInstance().font,
                listOf(
                    "jei.$ModId.need_mru".mcTranslate(recipe.mruPerTick * recipe.time).visualOrderText,
                    "jei.$ModId.during".mcTranslate(recipe.time).visualOrderText
                ),
                mouseX.toInt(),
                mouseY.toInt()
            )
    }

    companion object {
        @JvmField val RL_ID = "envoyer".ecRL
    }
}
