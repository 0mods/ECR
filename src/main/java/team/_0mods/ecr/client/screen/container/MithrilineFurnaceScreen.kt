package team._0mods.ecr.client.screen.container

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import team._0mods.ecr.ModId
import team._0mods.ecr.api.rl
import team._0mods.ecr.common.blocks.entity.MithrilineFurnaceEntity
import team._0mods.ecr.common.container.MithrilineFurnaceContainer

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
        private val texture = "$ModId:textures/gui/mithriline_furnace.png".rl
    }

    init {
        imageWidth = 175
        imageHeight = 166
    }

    override fun render(poseStack: PoseStack, mouseX: Int, mouseY: Int, partialTick: Float) {
        this.renderBackground(poseStack)
        super.render(poseStack, mouseX, mouseY, partialTick)
        this.renderTooltip(poseStack, mouseX, mouseY)
    }

    override fun renderLabels(poseStack: PoseStack, mouseX: Int, mouseY: Int) {}

    override fun renderBg(poseStack: PoseStack, partialTick: Float, mouseX: Int, mouseY: Int) {
        RenderSystem.setShader(GameRenderer::getPositionShader)
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f)
        RenderSystem.setShaderTexture(0, texture)

        blit(poseStack, this.guiLeft, this.guiTop, 0f, 0f, this.imageWidth, this.imageHeight, 256, 256)

        fill(poseStack, 7.xPos, 59.yPos, 23.xPos, 75.yPos, 0x66ff66)

        val be = menu.blockEntity
        if (be != null && be is MithrilineFurnaceEntity) {
            val mru = be.mruStorage

            if (isCursorAtPos(mouseX, mouseY, 6.xPos, 59.yPos, 18, 18))
                this.renderTooltip(poseStack, Component.literal("ESPE: ${mru.mruStorage}/${mru.maxMRUStorage}"), mouseX, mouseY)

            renderProgressArrow(poseStack, be)
        }
    }

    private fun renderProgressArrow(poseStack: PoseStack, be: MithrilineFurnaceEntity) {
        if (menu.hasActiveRecipe) {
            blit(poseStack, 7.xPos, 16.yPos, 176, 15, 8, menu.scaleProgress())
        }
    }

    private fun isCursorAtPos(cursorX: Int, cursorY: Int, x: Int, y: Int, width: Int, height: Int) : Boolean =
        cursorX >= x && cursorY >=y && cursorX <= x + width && cursorY <= y + height

    private val Int.xPos: Int get() {
        val j = ((this@MithrilineFurnaceScreen.width / 2) - (this@MithrilineFurnaceScreen.imageWidth / 2))
        return j + this
    }

    private val Int.yPos: Int get() {
        val j = ((this@MithrilineFurnaceScreen.height / 2) - (this@MithrilineFurnaceScreen.imageHeight / 2))
        return j + this
    }
}
