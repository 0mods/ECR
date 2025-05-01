package team._0mods.ecr.common.compact.rei.widget

import me.shedaniel.math.Point
import me.shedaniel.rei.api.client.gui.widgets.Widgets
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Renderable
import net.minecraft.client.gui.components.events.GuiEventListener
import team._0mods.ecr.api.utils.ecRL

class LittleArrowWidget(
    private val point: Point,
    private val isHorizontal: Boolean
): Renderable, GuiEventListener {
    private val texture = "textures/gui/widget/recipe_widgets.png".ecRL

    override fun render(
        guiGraphics: GuiGraphics,
        mouseX: Int,
        mouseY: Int,
        partialTick: Float
    ) {
        if (isHorizontal)
            guiGraphics.blit(texture, point.x, point.y, 0f, 10f, 24, 7, 150, 60)
        else
            guiGraphics.blit(texture, point.x, point.y, 54f, 0f, 7, 16, 150, 60)
    }

    override fun setFocused(focused: Boolean) {}

    override fun isFocused(): Boolean = true

    companion object {
        @JvmStatic
        fun createHorizontal(point: Point) = Widgets.wrapVanillaWidget(LittleArrowWidget(point, true))
        @JvmStatic
        fun createVertical(point: Point) = Widgets.wrapVanillaWidget(LittleArrowWidget(point, false))
    }
}