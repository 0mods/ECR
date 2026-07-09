package com.algorithmlx.ecr.api.client

import com.algorithmlx.ecr.api.mru.storage.MRUStorage
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.network.chat.Component
import java.awt.Color

fun isCursorAtPos(cursorX: Int, cursorY: Int, x: Int, y: Int, width: Int, height: Int) : Boolean =
    isCursorAtPos(cursorX.toDouble(), cursorY.toDouble(), x, y, width, height)

fun isCursorAtPos(cursorX: Double, cursorY: Double, x: Int, y: Int, width: Int, height: Int) : Boolean =
    cursorX >= x && cursorY >= y && cursorX <= x + width && cursorY <= y + height

fun drawMRULine(
    graphics: GuiGraphicsExtractor,
    storage: MRUStorage,
    x: Int,
    y: Int,
    xo: Int,
    yo: Int,
    width: Int,
    height: Int,
    mouseX: Int,
    mouseY: Int,
    colorIn: Int = Color(139, 0, 255).rgb,
    colorOut: Int = Color(50, 18, 122).rgb
) {
    drawMRUGradientLine(graphics, storage, x, y, xo, yo, height, width, colorIn, colorOut)
    if (isCursorAtPos(mouseX, mouseY, xo + x, yo + y, width, height)) {
        graphics.setTooltipForNextFrame(
            Component.literal("${storage.mruType.name.string}: ${storage.mru}/${storage.mruCapacity}"),
            mouseX, mouseY
        )
    }
}


fun drawMRUGradientLine(
    gg: GuiGraphicsExtractor,
    container: MRUStorage,
    x: Int,
    y: Int,
    xo: Int,
    yo: Int,
    height: Int,
    width: Int,
    colorIn: Int = Color(139, 0, 255).rgb,
    colorOut: Int = Color(50, 18, 122).rgb
) {
    val m = ((container.mru.toFloat() / container.mruCapacity) * width).toInt()
    gg.fillGradient(x + xo, y + yo, (x + m) + xo, (y + height) + yo, colorIn, colorOut)
}
