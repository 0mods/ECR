package team._0mods.ecr.client.screen.menu.widget

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.network.chat.Component
import team._0mods.ecr.api.client.defaultBlit
import team._0mods.ecr.api.utils.ecRL
import team._0mods.ecr.common.blocks.entity.MatrixDestructorEntity

class MatrixDestructorIndicator(x: Int, y: Int, private val status: MatrixDestructorEntity.MatrixDestructorStatus): AbstractWidget(x, y, 10, 10, Component.empty()) {
    companion object {
        private val texture = "textures/gui/widget/matrix_destructor_indicators.png".ecRL
    }

    override fun renderWidget(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        val x = when (status) {
            MatrixDestructorEntity.MatrixDestructorStatus.WORKING -> 10f
            MatrixDestructorEntity.MatrixDestructorStatus.STOPPED -> 10f
            else -> 0f
        }

        val y = when (status) {
            MatrixDestructorEntity.MatrixDestructorStatus.WARNING -> 10f
            MatrixDestructorEntity.MatrixDestructorStatus.STOPPED -> 10f
            else -> 0f
        }

        guiGraphics.defaultBlit(texture, this.x, this.y, x, y, 10, 10, 20, 20)
    }

    override fun updateWidgetNarration(narrationElementOutput: NarrationElementOutput) {}
}