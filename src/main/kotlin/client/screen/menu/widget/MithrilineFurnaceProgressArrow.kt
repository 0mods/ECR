package team._0mods.ecr.client.screen.menu.widget

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.network.chat.Component
import net.minecraft.util.Mth
import ru.hollowhorizon.hc.client.utils.defaultBlit
import team._0mods.ecr.api.utils.ecRL
import team._0mods.ecr.common.menu.MithrilineFurnaceMenu

class MithrilineFurnaceProgressArrow(x: Int, y: Int, private val menu: MithrilineFurnaceMenu) : AbstractWidget(x, y, 16, 8, Component.empty()) {
    companion object {
        private val texture = "textures/gui/widget/mithriline_furnace_arrow.png".ecRL
    }

    override fun renderWidget(gg: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        gg.defaultBlit(texture, this.x, this.y, 0f, 0f, 8, 16, 16, 16)
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