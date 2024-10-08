package team._0mods.ecr.api.client

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import team._0mods.ecr.api.mru.MRUContainer
import java.awt.Color

fun isCursorAtPos(cursorX: Int, cursorY: Int, x: Int, y: Int, width: Int, height: Int) : Boolean =
    cursorX >= x && cursorY >=y && cursorX <= x + width && cursorY <= y + height

fun PoseStack.blit(
    id: ResourceLocation,
    x: Int,
    y: Int,
    uOffset: Float = 0f,
    vOffset: Float = 0f,
    width: Int = 176,
    height: Int = 166,
    textureWidth: Int = 256,
    textureHeight: Int = 256
) {
    RenderSystem.setShader(GameRenderer::getPositionShader)
    RenderSystem.setShaderColor(1f, 1f, 1f, 1f)
    RenderSystem.setShaderTexture(0, id)
    AbstractContainerScreen.blit(
        this,
        x,
        y,
        uOffset,
        vOffset,
        width,
        height,
        textureWidth,
        textureHeight
    )
}

fun AbstractContainerScreen<*>.xPos(x: Int): Int {
    val j = ((this.width / 2) - (this.imageWidth / 2))
    return x + j
}

fun AbstractContainerScreen<*>.yPos(y: Int): Int {
    val j = ((this.height / 2) - (this.imageHeight / 2))
    return y + j
}

fun AbstractContainerScreen<*>.xPos(x: Float): Float {
    val j = ((this.width / 2) - (this.imageWidth / 2))
    return x + j
}

fun AbstractContainerScreen<*>.yPos(y: Float): Float {
    val j = ((this.height / 2) - (this.imageHeight / 2))
    return y + j
}

fun AbstractContainerScreen<*>.drawMRULine(
    poseStack: PoseStack,
    container: MRUContainer,
    x: Int,
    y: Int,
    width: Int,
    height: Int,
    mouseX: Int,
    mouseY: Int,
    colorIn: Int = Color(139, 0, 255).rgb,
    colorOut: Int = Color(50, 18, 122).rgb
) {
    this.drawMRUGradientLine(poseStack, container, x, y, height, width, colorIn, colorOut)
    if (isCursorAtPos(mouseX, mouseY, xPos(x), yPos(y), width, height))
        this.renderTooltip(
            poseStack,
            Component.literal("${container.mruType.display.string}: ${container.mruStorage}/${container.maxMRUStorage}"),
            mouseX,
            mouseY
        )
}

fun AbstractContainerScreen<*>.drawMRUGradientLine(
    poseStack: PoseStack,
    container: MRUContainer,
    x: Int,
    y: Int,
    height: Int,
    width: Int,
    colorIn: Int = Color(139, 0, 255).rgb,
    colorOut: Int = Color(50, 18, 122).rgb
) {
    val m = ((container.mruStorage.toFloat() / container.maxMRUStorage) * width).toInt()
    fillGradient(poseStack, xPos(x), yPos(y), xPos(x + m), yPos(y + height), colorIn, colorOut)
}
