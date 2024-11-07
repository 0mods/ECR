package team._0mods.ecr.client.screen.container

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import team._0mods.ecr.api.client.defaultBlit
import team._0mods.ecr.api.client.drawMRULine
import team._0mods.ecr.api.client.xPos
import team._0mods.ecr.api.client.yPos
import team._0mods.ecr.api.utils.ecRL
import team._0mods.ecr.client.screen.container.widget.MithrilineFurnaceProgressArrow
import team._0mods.ecr.common.blocks.entity.MithrilineFurnaceEntity
import team._0mods.ecr.common.container.MithrilineFurnaceContainer
import java.awt.Color

class MithrilineFurnaceScreen(
    menu: MithrilineFurnaceContainer,
    playerInventory: Inventory,
    title: Component
) : AbstractContainerScreen<MithrilineFurnaceContainer>(
    menu,
    playerInventory,
    title
) {
    companion object {
        private val texture = "textures/gui/mithriline_furnace.png".ecRL
    }

    override fun init() {
        super.init()
        addRenderableOnly(MithrilineFurnaceProgressArrow(xPos(84), yPos(41), this.menu))
    }

    override fun render(gg: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        this.renderBackground(gg)
        super.render(gg, mouseX, mouseY, partialTick)
        this.renderTooltip(gg, mouseX, mouseY)

        val be = menu.blockEntity
        if (be is MithrilineFurnaceEntity) {
            val mru = be.mruContainer

            this.drawMRULine(gg, mru, 8, 60, 16, 16, mouseX, mouseY, Color(113, 178, 123, 135).rgb, Color(113, 178, 123, 135).rgb)
        }
    }

    override fun renderLabels(gg: GuiGraphics, mouseX: Int, mouseY: Int) {}

    override fun renderBg(gg: GuiGraphics, partialTick: Float, mouseX: Int, mouseY: Int) {
        gg.defaultBlit(texture, this.guiLeft, this.guiTop)
    }
}
