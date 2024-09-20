package team._0mods.ecr.client.screen.container

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import ru.hollowhorizon.hc.client.utils.rl
import team._0mods.ecr.api.ModId
import team._0mods.ecr.api.client.blit
import team._0mods.ecr.api.client.isCursorAtPos
import team._0mods.ecr.api.client.xPos
import team._0mods.ecr.api.client.yPos
import team._0mods.ecr.client.screen.container.widget.MithrilineFurnaceProgressArrow
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

    override fun init() {
        super.init()
        addRenderableOnly(MithrilineFurnaceProgressArrow(xPos(84), yPos(41), this.menu))
    }

    override fun render(poseStack: PoseStack, mouseX: Int, mouseY: Int, partialTick: Float) {
        this.renderBackground(poseStack)
        super.render(poseStack, mouseX, mouseY, partialTick)
        this.renderTooltip(poseStack, mouseX, mouseY)

        val be = menu.blockEntity
        if (be is MithrilineFurnaceEntity) {
            val mru = be.mruContainer

            if (isCursorAtPos(mouseX, mouseY, xPos(6), yPos(59), 18, 18))
                this.renderTooltip(poseStack, Component.literal("${mru.mruType.display.string}: ${mru.mruStorage}/${mru.maxMRUStorage}"), mouseX, mouseY)
        }
    }

    override fun renderLabels(poseStack: PoseStack, mouseX: Int, mouseY: Int) {}

    override fun renderBg(poseStack: PoseStack, partialTick: Float, mouseX: Int, mouseY: Int) {
        poseStack.blit(texture, this.guiLeft, this.guiTop)
    }
}
