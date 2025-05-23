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
import team._0mods.ecr.client.screen.menu.widget.XLikeProgressArrow
import team._0mods.ecr.common.menu.XLikeMenu

open class XLikeScreen<T: XLikeMenu>(
    menu: T,
    inv: Inventory,
    title: Component,
    textureId: String
): AbstractContainerScreen<T>(
    menu, inv, title
) {
    private val texture = "textures/gui/$textureId.png".ecRL

    override fun render(gg: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        this.renderBackground(gg)
        super.render(gg, mouseX, mouseY, partialTick)
        this.renderTooltip(gg, mouseX, mouseY)
    }

    override fun renderLabels(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int) {}

    override fun renderBg(
        gg: GuiGraphics,
        partialTick: Float,
        mouseX: Int,
        mouseY: Int
    ) {
        gg.defaultBlit(texture, guiPosLeft, guiPosTop)
        val be = menu.blockEntity
        if (be != null) {
            val mru = be.mruContainer
            this.drawMRULine(gg, mru, 98, 17, 52, 8, mouseX, mouseY)
        }
    }

    class Envoyer(menu: XLikeMenu.Envoyer, inv: Inventory, title: Component): XLikeScreen<XLikeMenu.Envoyer>(menu, inv, title, "envoyer") {
        override fun init() {
            super.init()
            addRenderableOnly(XLikeProgressArrow(xPos(85), yPos(39), menu, "envoyer"))
        }
    }

    class MagicTable(menu: XLikeMenu.MagicTable, inv: Inventory, title: Component): XLikeScreen<XLikeMenu.MagicTable>(menu, inv, title, "envoyer") {
        override fun init() {
            super.init()
            addRenderableOnly(XLikeProgressArrow(xPos(85), yPos(39), menu, "envoyer"))
        }
    }
}