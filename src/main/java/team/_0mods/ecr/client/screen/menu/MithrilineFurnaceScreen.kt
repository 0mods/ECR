package team._0mods.ecr.client.screen.menu

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import team._0mods.ecr.api.client.*
import team._0mods.ecr.api.utils.ecRL
import team._0mods.ecr.client.screen.menu.widget.MithrilineFurnaceProgressArrow
import team._0mods.ecr.common.blocks.entity.MithrilineFurnaceEntity
import team._0mods.ecr.common.menu.MithrilineFurnaceMenu
import java.awt.Color

class MithrilineFurnaceScreen(
    menu: MithrilineFurnaceMenu,
    playerInventory: Inventory,
    title: Component
) : AbstractContainerScreen<MithrilineFurnaceMenu>(
    menu,
    playerInventory,
    title
), NoLabels {
    companion object {
        private val texture = "textures/gui/mithriline_furnace.png".ecRL
    }

    override fun init() {
        super.init()
        addRenderableOnly(MithrilineFurnaceProgressArrow(xPos(84), yPos(41), this.menu))
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        this.renderBackground(guiGraphics)
        super.render(guiGraphics, mouseX, mouseY, partialTick)
        this.renderTooltip(guiGraphics, mouseX, mouseY)

        val entity = menu.blockEntity
        if (entity is MithrilineFurnaceEntity) {
            val mru = entity.mruContainer

            this.drawMRULine(guiGraphics, mru, 8, 60, 16, 16, mouseX, mouseY, Color(113, 178, 123, 135).rgb, Color(113, 178, 123, 135).rgb)
        }
    }

    override fun renderBg(guiGraphics: GuiGraphics, partialTick: Float, mouseX: Int, mouseY: Int) {
        guiGraphics.defaultBlit(texture, this.guiPosLeft, this.guiPosTop)
    }
}