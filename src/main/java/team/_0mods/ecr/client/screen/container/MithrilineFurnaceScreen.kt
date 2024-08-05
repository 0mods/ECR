package team._0mods.ecr.client.screen.container

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import team._0mods.ecr.ModId
import team._0mods.ecr.common.container.MithrilineFurnaceContainer
import team._0mods.ecr.common.init.registry.ECCapabilities
import team._0mods.ecr.common.rl

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

        /*
        private static final int ENERGY_LEFT = 96;
    private static final int ENERGY_WIDTH = 72;
    private static final int ENERGY_TOP = 8;
    private static final int ENERGY_HEIGHT = 8;
        */

        //8, 60
        //graphics.fillGradient(leftPos + ENERGY_LEFT, topPos + ENERGY_TOP, leftPos + ENERGY_LEFT + p, topPos + ENERGY_TOP + ENERGY_HEIGHT, 0xffff0000, 0xff000000);
//        fill(poseStack,leftPos + ENERGY_LEFT, topPos + ENERGY_TOP, leftPos + ENERGY_LEFT + p, topPos + ENERGY_TOP + ENERGY_HEIGHT, 0xffff00)
        fill(poseStack, 8, 60/* + m*/, 23, 75, 0x66ff66)

        this.menu.blockEntity?.getCapability(ECCapabilities.MRU_CONTAINER)?.ifPresent {
            val m = (it.mruStorage / it.maxMRUStorage) * 16


        }
    }
}
