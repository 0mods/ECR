package team._0mods.ecr.client.screen.container.widget

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.network.chat.Component
import net.minecraft.util.Mth
import team._0mods.ecr.api.client.defaultBlit
import team._0mods.ecr.api.utils.ecRL
import team._0mods.ecr.common.container.MithrilineFurnaceContainer

class MithrilineFurnaceProgressArrow(x: Int, y: Int, private val menu: MithrilineFurnaceContainer) : AbstractWidget(x, y, 16, 8, Component.empty()) {
    companion object {
        private val texture = "textures/gui/widget/mithriline_furnace_arrow.png".ecRL
    }

    override fun renderWidget(gg: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        gg.defaultBlit(texture, this.x, this.y, width = 8, height = 16, textureWidth = 16, textureHeight = 16)
        renderProgressArrow(gg)
    }

    private fun renderProgressArrow(gg: GuiGraphics) {
        val progress = this.menu.data.get(0)
        val maxProgress = this.menu.data.get(1)

        if (maxProgress > 0) {
            val calc = progress.toFloat() / maxProgress.toFloat()
            val fl = Mth.floor(calc * 16)

            gg.defaultBlit(texture, this.x, this.y + (16 - fl), 8f, 16f - fl, 8, fl, 16, 16)
        }
    }

    override fun updateWidgetNarration(narrationElementOutput: NarrationElementOutput) {}
}