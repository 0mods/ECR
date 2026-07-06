package com.algorithmlx.ecr.client.book

import com.algorithmlx.ecr.api.ecRL
import com.algorithmlx.ecr.api.research.*
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.util.Mth
import java.awt.Color
import kotlin.math.pow

class BookBookmarkController {
    private val bookmarkTexture = "textures/gui/book/bookmark.png".ecRL
    private val selectedTexture = "textures/gui/book/bookmark_selected.png".ecRL
    private val progress = mutableMapOf<BookBookmarkKey, Float>()
    private var pageHover = 0f
    private var target: BookBookmarkKey? = null
    private var hue = 0f
    private var saturation = 0f
    private var value = 1f
    private var pickerX = -1
    private var pickerY = -1
    private var draggingPicker = false
    private var draggingHue = false
    private var draggingSaturation = false

    val isPickerOpen get() = target != null

    fun restore(state: BookViewState, screenWidth: Int, screenHeight: Int) {
        pickerX = state.pickerX.takeIf { it >= 0 } ?: (screenWidth - PICKER_WIDTH - 8)
        pickerY = state.pickerY.takeIf { it >= 0 } ?: 24
        constrain(screenWidth, screenHeight)
    }

    fun update(dt: Float, screenWidth: Int, mouseX: Int, mouseY: Int) {
        ClientResearchState.bookmarks().forEachIndexed { index, bookmark ->
            val key = BookBookmarkKey(bookmark.research, bookmark.spread)
            val x = screenWidth - 24 - index * 20
            val current = progress[key] ?: 0f
            val yOffset = Mth.lerp(current, -24f, 0f).toInt()
            val hovered = mouseX in x..x + 16 && mouseY in CATEGORY_HEIGHT..48 + yOffset
            progress[key] = approach(current, if (hovered) 1f else 0f, dt)
        }
    }

    fun renderGlobal(graphics: GuiGraphicsExtractor, screenWidth: Int, mouseX: Int, mouseY: Int) {
        graphics.enableScissor(0, CATEGORY_HEIGHT, screenWidth, graphics.guiHeight())
        ClientResearchState.bookmarks().forEachIndexed { index, bookmark ->
            val key = BookBookmarkKey(bookmark.research, bookmark.spread)
            val x = screenWidth - 24 - index * 20
            val yOffset = Mth.lerp(progress[key] ?: 0f, -24f, 0f).toInt()
            val color = 0xFF000000.toInt() or (bookmark.color and 0xFFFFFF)
            graphics.pose().pushMatrix()
            graphics.pose().translate((x + 16).toFloat(), yOffset.toFloat())
            graphics.pose().rotate(Math.toRadians(90.0).toFloat())
            graphics.blit(RenderPipelines.GUI_TEXTURED, selectedTexture, 0, 0, 0f, 0f, 48, 16, 48, 16, color)
            graphics.pose().popMatrix()
            if (mouseX in x..x + 16 && mouseY in CATEGORY_HEIGHT..48 + yOffset) {
                val title = ResearchCatalog.snapshot().entries[bookmark.research]?.title?.component()
                    ?: Component.literal(bookmark.research.toString())
                graphics.setTooltipForNextFrame(title, mouseX, mouseY.coerceAtLeast(CATEGORY_HEIGHT + 4))
            }
        }
        graphics.disableScissor()
    }

    fun selectGlobal(mouseX: Int, mouseY: Int, screenWidth: Int): BookBookmark? {
        ClientResearchState.bookmarks().forEachIndexed { index, bookmark ->
            val key = BookBookmarkKey(bookmark.research, bookmark.spread)
            val x = screenWidth - 24 - index * 20
            val yOffset = Mth.lerp(progress[key] ?: 0f, -24f, 0f).toInt()
            if (mouseX in x..x + 16 && mouseY in CATEGORY_HEIGHT..48 + yOffset) return bookmark
        }
        return null
    }

    fun renderPage(graphics: GuiGraphicsExtractor, entry: BookEntry, spread: Int, hovered: Boolean, dt: Float) {
        pageHover = approach(pageHover, if (hovered) 1f else 0f, dt)
        val bookmark = ClientResearchState.bookmark(entry.id, spread)
        val color = bookmark?.let { 0xFF000000.toInt() or (it.color and 0xFFFFFF) }
        val offset = Mth.lerp(pageHover, -6f, 18f).toInt()
        graphics.blit(
            RenderPipelines.GUI_TEXTURED,
            if (bookmark == null) bookmarkTexture else selectedTexture,
            BOOKMARK_X + offset,
            BOOKMARK_Y,
            0f,
            0f,
            48,
            16,
            48,
            16,
            color ?: 0xFFFFFFFF.toInt()
        )
    }

    fun open(entry: BookEntry, spread: Int) {
        target = BookBookmarkKey(entry.id, spread)
        val color = ClientResearchState.bookmark(entry.id, spread)?.color
        if (color == null) {
            hue = 0f
            saturation = 0f
            value = 1f
        } else {
            val hsb = Color.RGBtoHSB((color shr 16) and 0xFF, (color shr 8) and 0xFF, color and 0xFF, null)
            hue = hsb[0]
            saturation = hsb[1]
            value = hsb[2]
        }
    }

    fun close() {
        target = null
        stopDragging()
    }

    fun click(mouseX: Int, mouseY: Int): Boolean {
        if (target == null) return false
        val svX = pickerX + PADDING
        val svY = pickerY + HEADER_HEIGHT + PADDING
        val hueX = svX + SV_SIZE + PADDING
        val clearY = svY + HUE_HEIGHT + PADDING
        when {
            mouseX in pickerX until pickerX + PICKER_WIDTH && mouseY in pickerY until pickerY + HEADER_HEIGHT -> draggingPicker = true
            mouseX in svX until svX + SV_SIZE && mouseY in svY until svY + SV_SIZE -> {
                draggingSaturation = true
                updateSaturation(mouseX - svX, mouseY - svY)
            }
            mouseX in hueX until hueX + HUE_WIDTH && mouseY in svY until svY + HUE_HEIGHT -> {
                draggingHue = true
                updateHue(mouseY - svY)
            }
            mouseX in hueX until hueX + CLEAR_SIZE && mouseY in clearY until clearY + CLEAR_SIZE -> {
                target?.let { ResearchNetwork.updateFavorite(it.research, it.spread, null) }
                close()
            }
            else -> close()
        }
        return true
    }

    fun drag(mouseX: Int, mouseY: Int, dragX: Double, dragY: Double, screenWidth: Int, screenHeight: Int): Boolean {
        if (target == null) return false
        if (draggingPicker) {
            pickerX += dragX.toInt()
            pickerY += dragY.toInt()
            constrain(screenWidth, screenHeight)
            return true
        }
        val svX = pickerX + PADDING
        val svY = pickerY + HEADER_HEIGHT + PADDING
        if (draggingSaturation) updateSaturation(mouseX - svX, mouseY - svY)
        if (draggingHue) updateHue(mouseY - svY)
        return draggingSaturation || draggingHue
    }

    fun stopDragging() {
        draggingPicker = false
        draggingHue = false
        draggingSaturation = false
    }

    fun renderPicker(graphics: GuiGraphicsExtractor) {
        if (target == null) return
        val svX = pickerX + PADDING
        val svY = pickerY + HEADER_HEIGHT + PADDING
        graphics.fill(pickerX, pickerY, pickerX + PICKER_WIDTH, pickerY + PICKER_HEIGHT, 0xF0141820.toInt())
        graphics.fill(pickerX, pickerY, pickerX + PICKER_WIDTH, pickerY + HEADER_HEIGHT, 0xFF3A4656.toInt())
        val hueColor = 0xFF000000.toInt() or (Color.HSBtoRGB(hue, 1f, 1f) and 0xFFFFFF)
        graphics.fill(svX, svY, svX + SV_SIZE, svY + SV_SIZE, hueColor)
        repeat(SV_SIZE) { x ->
            val alpha = ((1f - x.toFloat() / SV_SIZE) * 255).toInt()
            graphics.fill(svX + x, svY, svX + x + 1, svY + SV_SIZE, (alpha shl 24) or 0xFFFFFF)
        }
        repeat(SV_SIZE) { y ->
            val alpha = (y.toFloat() / SV_SIZE * 255).toInt()
            graphics.fill(svX, svY + y, svX + SV_SIZE, svY + y + 1, alpha shl 24)
        }
        val cursorX = svX + (saturation * SV_SIZE).toInt()
        val cursorY = svY + ((1f - value) * SV_SIZE).toInt()
        graphics.fill(cursorX - 2, cursorY - 2, cursorX + 3, cursorY + 3, 0xFFFFFFFF.toInt())
        graphics.fill(cursorX - 1, cursorY - 1, cursorX + 2, cursorY + 2, 0xFF000000.toInt())
        val hueX = svX + SV_SIZE + PADDING
        repeat(HUE_HEIGHT) { y ->
            val color = 0xFF000000.toInt() or (Color.HSBtoRGB(y.toFloat() / HUE_HEIGHT, 1f, 1f) and 0xFFFFFF)
            graphics.fill(hueX, svY + y, hueX + HUE_WIDTH, svY + y + 1, color)
        }
        val hueY = svY + (hue * HUE_HEIGHT).toInt()
        graphics.fill(hueX - 1, hueY, hueX + HUE_WIDTH + 1, hueY + 1, 0xFFFFFFFF.toInt())
        val clearY = svY + HUE_HEIGHT + PADDING
        graphics.fill(hueX, clearY, hueX + CLEAR_SIZE, clearY + CLEAR_SIZE, 0xFF2A323E.toInt())
        graphics.outline(hueX, clearY, CLEAR_SIZE, CLEAR_SIZE, 0xFFFF6B7A.toInt())
    }

    fun appendTo(state: BookViewState): BookViewState = state.copy(pickerX = pickerX, pickerY = pickerY)

    private fun updateSaturation(x: Int, y: Int) {
        saturation = (x.toFloat() / SV_SIZE).coerceIn(0f, 1f)
        value = 1f - (y.toFloat() / SV_SIZE).coerceIn(0f, 1f)
        applyColor()
    }

    private fun updateHue(y: Int) {
        hue = (y.toFloat() / HUE_HEIGHT).coerceIn(0f, 1f)
        applyColor()
    }

    private fun applyColor() {
        val color = 0xFF000000.toInt() or (Color.HSBtoRGB(hue, saturation, value) and 0xFFFFFF)
        target?.let { ResearchNetwork.updateFavorite(it.research, it.spread, color) }
    }

    private fun constrain(screenWidth: Int, screenHeight: Int) {
        pickerX = pickerX.coerceIn(0, (screenWidth - PICKER_WIDTH).coerceAtLeast(0))
        pickerY = pickerY.coerceIn(0, (screenHeight - PICKER_HEIGHT).coerceAtLeast(0))
    }

    private fun approach(current: Float, target: Float, dt: Float): Float {
        if (dt <= 0f) return target
        return target + (current - target) * 0.5f.pow(dt / 0.08f)
    }

    private fun BookText.component(): Component = if (translated) Component.translatable(value) else Component.literal(value)

    private data class BookBookmarkKey(val research: Identifier, val spread: Int)

    companion object {
        const val BOOKMARK_X = 484
        const val BOOKMARK_Y = 16
        private const val CATEGORY_HEIGHT = 16
        private const val PADDING = 4
        private const val HEADER_HEIGHT = 7
        private const val SV_SIZE = 46
        private const val HUE_WIDTH = 10
        private const val HUE_HEIGHT = 46
        private const val CLEAR_SIZE = 10
        private const val PICKER_WIDTH = PADDING * 3 + SV_SIZE + HUE_WIDTH
        private const val PICKER_HEIGHT = HEADER_HEIGHT + PADDING * 2 + HUE_HEIGHT + PADDING + CLEAR_SIZE
    }
}
