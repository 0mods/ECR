package team._0mods.ecr.api.client

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import team._0mods.ecr.api.mru.MRUStorage
import java.awt.Color

fun isCursorAtPos(cursorX: Int, cursorY: Int, x: Int, y: Int, width: Int, height: Int) : Boolean =
    cursorX >= x && cursorY >= y && cursorX <= x + width && cursorY <= y + height

fun isCursorAtPos(cursorX: Double, cursorY: Double, x: Int, y: Int, width: Int, height: Int) : Boolean =
    cursorX >= x && cursorY >= y && cursorX <= x + width && cursorY <= y + height

fun GuiGraphics.defaultBlit(
    id: ResourceLocation,
    x: Int,
    y: Int,
    uOffset: Float = 0f,
    vOffset: Float = 0f,
    width: Int = 176,
    height: Int = 166,
    textureWidth: Int = 256,
    textureHeight: Int = 256
) = this.blit(id, x, y, uOffset, vOffset, width, height, textureWidth, textureHeight)

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
    gg: GuiGraphics,
    container: MRUStorage,
    x: Int,
    y: Int,
    width: Int,
    height: Int,
    mouseX: Int,
    mouseY: Int,
    colorIn: Int = Color(139, 0, 255).rgb,
    colorOut: Int = Color(50, 18, 122).rgb
) {
    this.drawMRUGradientLine(gg, container, x, y, height, width, colorIn, colorOut)
    if (isCursorAtPos(mouseX, mouseY, xPos(x), yPos(y), width, height)) {
        gg.renderTooltip(
            Minecraft.getInstance().font,
            Component.literal("${container.mruType.displayName.string}: ${container.mruStorage}/${container.maxMRUStorage}"),
            mouseX,
            mouseY
        )
    }
}

fun AbstractContainerScreen<*>.drawMRUGradientLine(
    gg: GuiGraphics,
    container: MRUStorage,
    x: Int,
    y: Int,
    height: Int,
    width: Int,
    colorIn: Int = Color(139, 0, 255).rgb,
    colorOut: Int = Color(50, 18, 122).rgb
) {
    val m = ((container.mruStorage.toFloat() / container.maxMRUStorage) * width).toInt()
    gg.fillGradient(xPos(x), yPos(y), xPos(x + m), yPos(y + height), colorIn, colorOut)
}
