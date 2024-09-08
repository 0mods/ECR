package team._0mods.ecr.client.screen.container.widget

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.network.chat.Component
import net.minecraft.util.Mth
import ru.hollowhorizon.hc.client.utils.rl
import team._0mods.ecr.ModId
import team._0mods.ecr.api.client.blit
import team._0mods.ecr.common.container.MithrilineFurnaceContainer

class MithrilineFurnaceProgressArrow(x: Int, y: Int, private val menu: MithrilineFurnaceContainer) : AbstractWidget(x, y, 16, 8, Component.empty()) {
    companion object {
        private val texture = "$ModId:textures/gui/widget/mithriline_furnace_arrow.png".rl
    }

    override fun render(poseStack: PoseStack, mouseX: Int, mouseY: Int, partialTick: Float) {
        poseStack.blit(texture, this.x, this.y, width = 8, height = 16, textureWidth = 16, textureHeight = 16)
        renderProgressArrow(poseStack)
    }

    private fun renderProgressArrow(poseStack: PoseStack) {
        val progress = this.menu.data.get(0)
        val maxProgress = this.menu.data.get(1)

        if (maxProgress > 0) {
            val calc = progress.toFloat() / maxProgress.toFloat()
            val fl = Mth.floor(calc * 16)

            poseStack.blit(texture, this.x, this.y + (16 - fl), 8f, 16f - fl, 8, fl, 16, 16)
        }
    }

    override fun updateNarration(narrationElementOutput: NarrationElementOutput) {}
}