package com.algorithmlx.ecr.client.screen

import com.algorithmlx.ecr.api.client.drawMRULine
import com.algorithmlx.ecr.api.ecRL
import com.algorithmlx.ecr.common.block.entity.MithrilineFurnaceEntity
import com.algorithmlx.ecr.common.menu.MithrilineFurnaceMenu
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import java.awt.Color

class MithrilineFurnaceScreen(
    menu: MithrilineFurnaceMenu,
    inv: Inventory,
    title: Component
): AbstractContainerScreen<MithrilineFurnaceMenu>(menu, inv, title) {
    private val texture = "".ecRL
    private val color = Color(113, 178, 123, 135).rgb

    override fun extractBackground(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, a: Float) {
        super.extractBackground(graphics, mouseX, mouseY, a)
        graphics.blit(
            RenderPipelines.GUI_TEXTURED,
            texture,
            this.leftPos, this.topPos,
            0F, 0F,
            this.imageHeight, this.imageWidth,
            256, 256
        )

        val be = menu.blockEntity
        if (be is MithrilineFurnaceEntity) {
            val mru = be.mruStorage
            drawMRULine(
                graphics, mru,
                8, 60,
                leftPos, topPos,
                16, 16,
                mouseX, mouseY,
                color, color
            )
        }
    }
}