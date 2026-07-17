package com.algorithmlx.ecr.client.screen

import com.algorithmlx.ecr.api.client.drawMRULine
import com.algorithmlx.ecr.client.widget.EnvoyerProgressArrow
import com.algorithmlx.ecr.common.block.entity.EnvoyerBlockEntity
import com.algorithmlx.ecr.common.init.ECRModIDs
import com.algorithmlx.ecr.common.menu.EnvoyerMenu
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory

class EnvoyerMenuScreen(
    menu: EnvoyerMenu,
    inv: Inventory,
    title: Component
): AbstractContainerScreen<EnvoyerMenu>(menu, inv, title) {
    override fun init() {
        super.init()
        addRenderableOnly(EnvoyerProgressArrow(this.leftPos + 85, this.topPos + 39, this.menu))
    }

    override fun extractBackground(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, a: Float) {
        super.extractBackground(graphics, mouseX, mouseY, a)
        graphics.blit(
            RenderPipelines.GUI_TEXTURED,
            TEXTURE,
            this.leftPos, this.topPos,
            0F, 0F,
            this.imageWidth, this.imageHeight,
            256, 256
        )

        val be = menu.blockEntity as? EnvoyerBlockEntity ?: return
        val mru = be.mruStorage
        drawMRULine(
            graphics, mru,
            98, 17,
            this.leftPos, this.topPos,
            52, 8,
            mouseX, mouseY
        )
    }

    override fun extractLabels(graphics: GuiGraphicsExtractor, xm: Int, ym: Int) {}

    companion object {
        private val TEXTURE = ECRModIDs.guiLocation(ECRModIDs.ENVOYER)
    }
}
