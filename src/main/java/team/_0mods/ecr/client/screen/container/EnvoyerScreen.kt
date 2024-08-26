package team._0mods.ecr.client.screen.container

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import team._0mods.ecr.common.container.EnvoyerContainer

class EnvoyerScreen(menu: EnvoyerContainer,
                    playerInventory: Inventory, title: Component
) : AbstractContainerScreen<EnvoyerContainer>(menu, playerInventory, title) {
    override fun renderBg(poseStack: PoseStack, partialTick: Float, mouseX: Int, mouseY: Int) {

    }
}