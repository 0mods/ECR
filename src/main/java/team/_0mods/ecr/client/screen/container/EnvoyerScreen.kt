package team._0mods.ecr.client.screen.container

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import team._0mods.ecr.ModId
import team._0mods.ecr.api.client.blit
import team._0mods.ecr.api.utils.rl
import team._0mods.ecr.common.container.EnvoyerContainer

class EnvoyerScreen(
    menu: EnvoyerContainer,
    playerInventory: Inventory,
    title: Component
) : AbstractContainerScreen<EnvoyerContainer>(menu, playerInventory, title) {
    companion object {
        val texture = "$ModId:textures/gui/envoyer.png".rl
    }

    override fun render(poseStack: PoseStack, mouseX: Int, mouseY: Int, partialTick: Float) {
        this.renderBackground(poseStack)
        super.render(poseStack, mouseX, mouseY, partialTick)
        this.renderTooltip(poseStack, mouseX, mouseY)
    }

    override fun renderBg(poseStack: PoseStack, partialTick: Float, mouseX: Int, mouseY: Int) {
        poseStack.blit(texture, this.guiLeft, this.guiTop)
    }
}
