package team._0mods.ecr.api.client

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import ru.hollowhorizon.hc.client.utils.isCursorAtPos
import ru.hollowhorizon.hc.client.utils.xPos
import ru.hollowhorizon.hc.client.utils.yPos
import team._0mods.ecr.api.mru.MRUStorage
import java.awt.Color

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
