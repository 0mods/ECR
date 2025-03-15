package team._0mods.ecr.api.client

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import ru.hollowhorizon.hc.client.utils.defaultBlit as hc_defaultBlit
import ru.hollowhorizon.hc.client.utils.guiPosLeft as hc_guiPosLeft
import ru.hollowhorizon.hc.client.utils.guiPosTop as hc_guiPosTop
import ru.hollowhorizon.hc.client.utils.isCursorAtPos as hc_isCursorAtPos
import ru.hollowhorizon.hc.client.utils.xPos as hc_xPos
import ru.hollowhorizon.hc.client.utils.yPos as hc_yPos
import team._0mods.ecr.api.mru.MRUStorage
import java.awt.Color

@Deprecated("Use HC format", ReplaceWith(
    "isCursorAtPos(cursorX, cursorY, x, y, width, height)",
    "ru.hollowhorizon.hc.client.utils.isCursorAtPos"
)
)
fun isCursorAtPos(cursorX: Int, cursorY: Int, x: Int, y: Int, width: Int, height: Int) : Boolean = hc_isCursorAtPos(cursorX, cursorY, x, y, width, height)

@Deprecated("Use HC format", ReplaceWith(
    "isCursorAtPos(cursorX, cursorY, x, y, width, height)",
    "ru.hollowhorizon.hc.client.utils.isCursorAtPos"
)
)
fun isCursorAtPos(cursorX: Double, cursorY: Double, x: Int, y: Int, width: Int, height: Int) : Boolean = hc_isCursorAtPos(cursorX, cursorY, x, y, width, height)

@Deprecated("Use HC format", ReplaceWith(
    "this.defaultBlit(id, x, y, uOffset, vOffset, width, height, textureWidth, textureHeight)",
    "ru.hollowhorizon.hc.client.utils.defaultBlit"
)
)
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
) = this.hc_defaultBlit(id, x, y, uOffset, vOffset, width, height, textureWidth, textureHeight)

@Deprecated("Use HC format", ReplaceWith("this.xPos(x)", "ru.hollowhorizon.hc.client.utils.xPos"))
fun AbstractContainerScreen<*>.xPos(x: Int): Int = this.hc_xPos(x)

@Deprecated("Use HC format", ReplaceWith("this.yPos(y)", "ru.hollowhorizon.hc.client.utils.yPos"))
fun AbstractContainerScreen<*>.yPos(y: Int): Int = this.hc_yPos(y)

@Deprecated("Use HC format", ReplaceWith("this.xPos(x)", "ru.hollowhorizon.hc.client.utils.xPos"))
fun AbstractContainerScreen<*>.xPos(x: Float): Float = this.hc_xPos(x)

@Deprecated("Use HC format", ReplaceWith("this.yPos(y)", "ru.hollowhorizon.hc.client.utils.yPos"))
fun AbstractContainerScreen<*>.yPos(y: Float): Float = this.hc_yPos(y)

fun AbstractContainerScreen<*>.drawMRULine(
    gg: GuiGraphics,
    container: MRUStorage,
    x: Int,
    y: Int,
    width: Int,
    height: Int,
    mouseX: Int,
    mouseY: Int,
    // Deprecated lol
    colorIn: Int = Color(139, 0, 255).rgb,
    colorOut: Int = Color(50, 18, 122).rgb
) {
    this.drawMRUGradientLine(gg, container, x, y, height, width, colorIn, colorOut)
    if (isCursorAtPos(mouseX, mouseY, xPos(x), yPos(y), width, height)) {
        gg.renderTooltip(
            Minecraft.getInstance().font,
            Component.literal("${container.mruType.displayName.string}: ${container.mru}/${container.maxMRUStorage}"),
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
    val m = ((container.mru.toFloat() / container.maxMRUStorage) * width).toInt()
    gg.fillGradient(xPos(x), yPos(y), xPos(x + m), yPos(y + height), colorIn, colorOut)
}
