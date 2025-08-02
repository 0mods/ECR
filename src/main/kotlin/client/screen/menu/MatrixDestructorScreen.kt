package team._0mods.ecr.client.screen.menu

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import ru.hollowhorizon.hc.client.utils.defaultBlit
import ru.hollowhorizon.hc.client.utils.guiPosLeft
import ru.hollowhorizon.hc.client.utils.guiPosTop
import ru.hollowhorizon.hc.client.utils.xPos
import ru.hollowhorizon.hc.client.utils.yPos
import team._0mods.ecr.api.client.*
import team._0mods.ecr.api.utils.ecRL
import team._0mods.ecr.common.blocks.entity.MatrixDestructorEntity
import team._0mods.ecr.common.menu.MatrixDestructorMenu

class MatrixDestructorScreen(
    menu: MatrixDestructorMenu,
    playerInventory: Inventory,
    title: Component
) : AbstractContainerScreen<MatrixDestructorMenu>(
    menu,
    playerInventory,
    title
) {
    companion object {
        private val texture = "textures/gui/matrix_destructor.png".ecRL
        private val textureIndicator = "textures/gui/widget/matrix_destructor_indicators.png".ecRL
    }

    override fun render(gg: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        this.renderBackground(gg)
        super.render(gg, mouseX, mouseY, partialTick)
        this.renderTooltip(gg, mouseX, mouseY)
    }

    override fun renderBg(gg: GuiGraphics, partialTick: Float, mouseX: Int, mouseY: Int) {
        gg.defaultBlit(texture, guiPosLeft, guiPosTop)

        val be = menu.blockEntity
        if (be is MatrixDestructorEntity) {
            val mru = be.mruContainer
            val status = be.status
            this.drawMRULine(gg, mru, 37, 17, 102, 10, mouseX, mouseY)

            if (status != null) {
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

                gg.defaultBlit(textureIndicator, xPos(83), yPos(36), x, y, 10, 10, 20, 20)
            }
        }
    }
}
