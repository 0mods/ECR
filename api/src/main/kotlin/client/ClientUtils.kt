package com.algorithmlx.ecr.api.client

import com.algorithmlx.ecr.api.mru.storage.MRUStorage
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.network.chat.Component
import java.awt.Color
import kotlin.math.abs
import kotlin.math.exp

fun isCursorAtPos(cursorX: Int, cursorY: Int, x: Int, y: Int, width: Int, height: Int) : Boolean =
    isCursorAtPos(cursorX.toDouble(), cursorY.toDouble(), x, y, width, height)

fun isCursorAtPos(cursorX: Double, cursorY: Double, x: Int, y: Int, width: Int, height: Int) : Boolean =
    cursorX >= x && cursorY >= y && cursorX <= x + width && cursorY <= y + height

class MRULineAnimation(
    private val smoothingSpeed: Double = DEFAULT_SMOOTHING_SPEED
) {
    private var previousFill = 0.0
    private var currentFill = 0.0
    private var initialized = false
    private val tickBlend = 1.0 - exp(-smoothingSpeed / TICKS_PER_SECOND)

    init {
        require(smoothingSpeed.isFinite() && smoothingSpeed > 0.0) {
            "MRU line smoothing speed must be finite and positive"
        }
    }

    fun tick(storage: MRUStorage) = tick(storage.mru, storage.mruCapacity)

    internal fun tick(mru: Int, capacity: Int) {
        val targetFill = calculateMRUFill(mru, capacity)
        if (!initialized) {
            initialize(targetFill)
            return
        }

        previousFill = currentFill
        currentFill += (targetFill - currentFill) * tickBlend
        if (abs(targetFill - currentFill) < SNAP_THRESHOLD) currentFill = targetFill
        currentFill = currentFill.coerceIn(0.0, 1.0)
    }

    fun sample(storage: MRUStorage, partialTick: Float): Double =
        sample(storage.mru, storage.mruCapacity, partialTick)

    internal fun sample(mru: Int, capacity: Int, partialTick: Float): Double {
        if (!initialized) initialize(calculateMRUFill(mru, capacity))
        val progress = partialTick.coerceIn(0F, 1F).toDouble()
        return previousFill + (currentFill - previousFill) * progress
    }

    private fun initialize(fill: Double) {
        initialized = true
        previousFill = fill
        currentFill = fill
    }

    companion object {
        private const val DEFAULT_SMOOTHING_SPEED = 10.0
        private const val TICKS_PER_SECOND = 20.0
        private const val SNAP_THRESHOLD = 0.0001
    }
}

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
    colorOut: Int = Color(50, 18, 122).rgb,
    animation: MRULineAnimation? = null,
    partialTick: Float = 1F
) {
    drawMRUGradientLine(
        graphics, storage, x, y, xo, yo, height, width, colorIn, colorOut, animation, partialTick
    )
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
    colorOut: Int = Color(50, 18, 122).rgb,
    animation: MRULineAnimation? = null,
    partialTick: Float = 1F
) {
    val fill = animation?.sample(container, partialTick) ?: calculateMRUFill(container.mru, container.mruCapacity)
    val m = calculateMRULineWidth(fill, width)
    if (m == 0) return

    gg.fillGradient(x + xo, y + yo, (x + m) + xo, (y + height) + yo, colorIn, colorOut)
}

fun calculateMRULineWidth(mru: Int, capacity: Int, width: Int): Int {
    return calculateMRULineWidth(calculateMRUFill(mru, capacity), width)
}

private fun calculateMRUFill(mru: Int, capacity: Int): Double =
    if (mru <= 0 || capacity <= 0) 0.0 else (mru.toDouble() / capacity).coerceIn(0.0, 1.0)

private fun calculateMRULineWidth(fill: Double, width: Int): Int =
    if (width <= 0) 0 else (fill.coerceIn(0.0, 1.0) * width).toInt()
