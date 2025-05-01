package team._0mods.ecr.client.screen.menu.widget

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.network.chat.Component
import ru.hollowhorizon.hc.client.utils.defaultBlit
import team._0mods.ecr.api.utils.ecRL
import team._0mods.ecr.common.menu.XLikeMenu

class XLikeProgressArrow(x: Int, y: Int, private val menu: XLikeMenu, id: String): AbstractWidget(x, y, 16, 8, Component.empty()){
    private val texture = "textures/gui/widget/${id}_arrow.png".ecRL

    override fun renderWidget(gg: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        gg.defaultBlit(texture, this.x, this.y, 0f, 0f, 24, 8, 24, 16)
        renderProgressArrow(gg)
    }

    private fun renderProgressArrow(gg: GuiGraphics) {
        val progress = this.menu.data.get(0)
        val maxProgress = this.menu.data.get(1)

        if (maxProgress > 0) {
            val m = ((progress.toFloat() / maxProgress.toFloat()) * 24).toInt()
            gg.defaultBlit(texture, this.x, this.y, vOffset = 8f, width = m, height = 8, textureWidth = 24, textureHeight = 16)
        }
    }

    override fun updateWidgetNarration(narrationElementOutput: NarrationElementOutput) {}
}