package team._0mods.ecr.client.screen.container

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import ru.hollowhorizon.hc.client.utils.rl
import team._0mods.ecr.api.ModId
import team._0mods.ecr.api.client.defaultBlit
import team._0mods.ecr.api.client.drawMRULine
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
        private val texture = "$ModId:textures/gui/matrix_destructor.png".rl
    }

    override fun render(gg: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        this.renderBackground(gg)
        super.render(gg, mouseX, mouseY, partialTick)
        this.renderTooltip(gg, mouseX, mouseY)
    }

    override fun renderBg(gg: GuiGraphics, partialTick: Float, mouseX: Int, mouseY: Int) {
        gg.defaultBlit(texture, this.guiLeft, this.guiTop)

        val be = menu.blockEntity
        if (be is MatrixDestructorEntity) {
            val mru = be.mruContainer
            this.drawMRULine(gg, mru, 38, 22, 100, 8, mouseX, mouseY)
        }
    }
}
