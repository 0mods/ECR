package team._0mods.ecr.client.screen.container

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import team._0mods.ecr.ModId
import team._0mods.ecr.api.client.blit
import team._0mods.ecr.api.client.isCursorAtPos
import team._0mods.ecr.api.client.xPos
import team._0mods.ecr.api.client.yPos
import team._0mods.ecr.api.utils.rl
import team._0mods.ecr.common.blocks.entity.MatrixDestructorEntity
import team._0mods.ecr.common.container.MatrixDestructorContainer
import java.awt.Color

class MatrixDestructorScreen(
    menu: MatrixDestructorContainer,
    playerInventory: Inventory,
    title: Component
) : AbstractContainerScreen<MatrixDestructorContainer>(
    menu,
    playerInventory,
    title
) {
    companion object {
        private val texture = "$ModId:textures/gui/matrix_destructor.png".rl
    }

    override fun render(poseStack: PoseStack, mouseX: Int, mouseY: Int, partialTick: Float) {
        this.renderBackground(poseStack)
        super.render(poseStack, mouseX, mouseY, partialTick)
        this.renderTooltip(poseStack, mouseX, mouseY)
    }

    override fun renderBg(poseStack: PoseStack, partialTick: Float, mouseX: Int, mouseY: Int) {
        poseStack.blit(texture, this.guiLeft, this.guiTop)

        val be = menu.blockEntity
        if (be is MatrixDestructorEntity) {
            val mru = be.mruStorage
            val startX = 38
            val startY = 22
            val width = 100
            val height = 8
            val m = ((mru.mruStorage.toFloat() / mru.maxMRUStorage) * width).toInt()

            fillGradient(poseStack, xPos(startX), yPos(startY), xPos(startX + m), yPos(startY + height), Color(139, 0, 255).rgb, Color(50, 18, 122).rgb)
            if (isCursorAtPos(mouseX, mouseY, xPos(startX), yPos(startY), width, height))
                this.renderTooltip(poseStack, Component.literal("${mru.mruType.display.string}: ${mru.mruStorage}/${mru.maxMRUStorage}"), mouseX, mouseY)
        }
    }
}
