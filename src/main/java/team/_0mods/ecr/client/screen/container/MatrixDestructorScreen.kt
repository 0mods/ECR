package team._0mods.ecr.client.screen.container

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import team._0mods.ecr.ModId
import team._0mods.ecr.api.client.isCursorAtPos
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
        RenderSystem.setShader(GameRenderer::getPositionShader)
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f)
        RenderSystem.setShaderTexture(0, texture)
        blit(poseStack, this.guiLeft, this.guiTop, 0f, 0f, this.imageWidth, this.imageHeight, 256, 256)

        val be = menu.blockEntity
        if (be is MatrixDestructorEntity) {
            val mru = be.mruStorage
            val m = ((mru.mruStorage / mru.maxMRUStorage.toFloat()) * 38).toInt()
            val startX = 38
            val startY = 22
            /*
            private static final int ENERGY_LEFT = 96;
            private static final int ENERGY_WIDTH = 72;
            private static final int ENERGY_TOP = 8;
            private static final int ENERGY_HEIGHT = 8;
            int p = (int) ((power / (float) GeneratorBlockEntity.CAPACITY) * ENERGY_WIDTH);
            */
            //graphics.fillGradient(leftPos + ENERGY_LEFT, topPos + ENERGY_TOP, leftPos + ENERGY_LEFT + p, topPos + ENERGY_TOP + ENERGY_HEIGHT, 0xffff0000, 0xff000000);
            fill(poseStack, startX.xPos, startY.yPos, (startX + m).xPos, (startY + 8).yPos, Color(139, 0, 255).rgb)
            if (isCursorAtPos(mouseX, mouseY, 37.xPos, 21.yPos, 102, 10))
                this.renderTooltip(poseStack, Component.literal("${mru.mruType.display.string}: ${mru.mruStorage}/${mru.maxMRUStorage}"), mouseX, mouseY)
        }
    }

    private val Int.xPos: Int get() {
        val j = ((this@MatrixDestructorScreen.width / 2) - (this@MatrixDestructorScreen.imageWidth / 2))
        return j + this
    }

    private val Int.yPos: Int get() {
        val j = ((this@MatrixDestructorScreen.height / 2) - (this@MatrixDestructorScreen.imageHeight / 2))
        return j + this
    }

    private val Float.xPos: Float get() {
        val j = ((this@MatrixDestructorScreen.width / 2) - (this@MatrixDestructorScreen.imageWidth / 2))
        return j + this
    }

    private val Float.yPos: Float get() {
        val j = ((this@MatrixDestructorScreen.height / 2) - (this@MatrixDestructorScreen.imageHeight / 2))
        return j + this
    }
}