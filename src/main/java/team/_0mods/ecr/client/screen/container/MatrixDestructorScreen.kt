package team._0mods.ecr.client.screen.container

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import ru.hollowhorizon.hc.client.utils.rl
import team._0mods.ecr.api.ModId
import team._0mods.ecr.api.client.blit
import team._0mods.ecr.api.client.drawMRULine
import team._0mods.ecr.common.blocks.entity.MatrixDestructorEntity
import team._0mods.ecr.common.container.MatrixDestructorContainer

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
            val mru = be.mruContainer
            this.drawMRULine(poseStack, mru, 38, 22, 100, 8, mouseX, mouseY)
        }
    }
}
