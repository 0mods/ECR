package team._0mods.ecr.common.compact.rei.widget

import me.shedaniel.math.Point
import me.shedaniel.rei.api.client.gui.widgets.Widgets
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Renderable
import net.minecraft.client.gui.components.events.GuiEventListener
import ru.hollowhorizon.hc.client.utils.isCursorAtPos
import ru.hollowhorizon.hc.common.utils.mcTranslate
import team._0mods.ecr.api.ModId
import team._0mods.ecr.api.utils.ecRL
import java.awt.Color

class MRULineWidget(
    private val point: Point,
    private val needMRU: Int,
    private val time: Int
): Renderable, GuiEventListener {
    private val texture = "textures/gui/widget/recipe_widgets.png".ecRL

    override fun render(
        guiGraphics: GuiGraphics,
        mouseX: Int,
        mouseY: Int,
        partialTick: Float
    ) {
        guiGraphics.blit(texture, point.x, point.y, 0f, 0f, 54, 10, 150, 60)
        guiGraphics.fillGradient(
            point.x + 1,
            point.y + 1,
            point.x + 53,
            point.y + 9,
            Color(139, 0, 255).rgb,
            Color(50, 18, 122).rgb
        )

        if (isCursorAtPos(mouseX, mouseY, point.x, point.y, 52, 8))
            guiGraphics.renderTooltip(
                Minecraft.getInstance().font,
                listOf(
                    "tooltip.$ModId.need_mru".mcTranslate(this.needMRU).visualOrderText,
                    "tooltip.$ModId.during".mcTranslate(this.time).visualOrderText
                ),
                mouseX,
                mouseY
            )
    }

    override fun setFocused(focused: Boolean) {}

    override fun isFocused(): Boolean = true

    companion object {
        fun createWidget(point: Point, needMRU: Int, time: Int) =
            Widgets.wrapVanillaWidget(MRULineWidget(point, needMRU, time))
    }
}