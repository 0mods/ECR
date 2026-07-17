package com.algorithmlx.ecr.client.book.controller

import com.algorithmlx.ecr.api.client.render.MultiblockPreviewGuiBridge
import com.algorithmlx.ecr.api.client.render.MultiblockPreviewRenderState
import com.algorithmlx.ecr.api.client.render.MultiblockPreviewTransform
import com.algorithmlx.ecr.api.client.research.BookElementRenderContext
import com.algorithmlx.ecr.api.utils.ecRL
import com.algorithmlx.ecr.api.multiblock.Multiblock
import com.algorithmlx.ecr.api.research.content.MultiblockBookElement
import com.mojang.blaze3d.platform.InputConstants
import com.mojang.blaze3d.platform.cursor.CursorTypes
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.resources.Identifier
import kotlin.math.exp
import kotlin.math.max
import kotlin.math.roundToInt

object MultiblockBookPreviewController {
    private val arrowLeft = "textures/gui/book/arrow_left.png".ecRL
    private val arrowLeftSelected = "textures/gui/book/arrow_left_selected.png".ecRL
    private val arrowRight = "textures/gui/book/arrow_right.png".ecRL
    private val arrowRightSelected = "textures/gui/book/arrow_right_selected.png".ecRL
    private val fullModeTexture = "textures/gui/book/multiblock.png".ecRL
    private val layeredModeTexture = "textures/gui/book/multiblock_layered.png".ecRL

    private val states = linkedMapOf<String, PreviewState>()
    private var frame = 0L
    private var draggingKey: String? = null
    private var draggingButton: Int? = null

    fun beginFrame() {
        frame++
    }

    fun clear() {
        states.clear()
        draggingKey = null
        draggingButton = null
    }

    fun render(
        context: BookElementRenderContext,
        element: MultiblockBookElement,
        multiblock: Multiblock
    ) {
        val key = context.interactionKey ?: return renderStatic(context, element, multiblock)
        val state = states.getOrPut(key) { PreviewState.from(element, multiblock) }
        state.lastSeenFrame = frame
        state.maxLayer = (multiblock.ySize - 1).coerceAtLeast(0)
        state.layer = state.layer.coerceIn(0, state.maxLayer)

        val previewLocalHeight = (context.height - CONTROL_RESERVED_HEIGHT).coerceAtLeast(1)
        val previewScreenHeight = (previewLocalHeight * context.scale).roundToInt()
            .coerceIn(1, context.screenHeight.coerceAtLeast(1))

        state.previewBounds = Rect(
            context.screenX,
            context.screenY,
            context.screenWidth.coerceAtLeast(1),
            previewScreenHeight
        )

        val controls = controlLayout(context)
        state.leftButton = controls.left.toScreen(context)
        state.modeButton = controls.mode.toScreen(context)
        state.rightButton = controls.right.toScreen(context)

        MultiblockPreviewGuiBridge.add(
            context.graphics,
            MultiblockPreviewRenderState(
                multiblock,
                state.transform(),
                context.screenX,
                context.screenY,
                context.screenX + context.screenWidth,
                context.screenY + previewScreenHeight
            )
        )

        renderControls(context, state, multiblock, controls)
    }

    private fun renderStatic(
        context: BookElementRenderContext,
        element: MultiblockBookElement,
        multiblock: Multiblock
    ) {
        MultiblockPreviewGuiBridge.add(
            context.graphics,
            MultiblockPreviewRenderState(
                multiblock,
                MultiblockPreviewTransform(
                    scale = element.scale,
                    rotationX = element.rotationX,
                    rotationY = element.rotationY,
                    layer = element.layer
                ),
                context.screenX,
                context.screenY,
                context.screenX + context.screenWidth,
                context.screenY + context.screenHeight
            )
        )
    }

    private fun renderControls(
        context: BookElementRenderContext,
        state: PreviewState,
        multiblock: Multiblock,
        controls: Controls
    ) {
        val leftHovered = controls.left.contains(context.mouseX, context.mouseY)
        val modeHovered = controls.mode.contains(context.mouseX, context.mouseY)
        val rightHovered = controls.right.contains(context.mouseX, context.mouseY)
        val buttonsHovered = leftHovered || modeHovered || rightHovered
        val previewHovered = context.mouseX in context.x until context.x + context.width &&
            context.mouseY in context.y until context.y + (context.height - CONTROL_RESERVED_HEIGHT).coerceAtLeast(1)

        if (buttonsHovered) {
            context.graphics.requestCursor(CursorTypes.POINTING_HAND)
        } else if (previewHovered) {
            val panning = draggingKey == context.interactionKey &&
                (draggingButton == RIGHT_MOUSE_BUTTON || shiftDown())
            context.graphics.requestCursor(if (panning) CursorTypes.RESIZE_ALL else CursorTypes.CROSSHAIR)
        }

        val canGoLeft = state.layered && state.layer > 0
        val canGoRight = state.layered && state.layer < state.maxLayer
        renderArrowButton(
            context,
            controls.left,
            if (leftHovered && canGoLeft) arrowLeftSelected else arrowLeft,
            canGoLeft
        )

        val modeTexture = if (state.layered) layeredModeTexture else fullModeTexture
        val modeTextureSize = if (state.layered) LAYERED_MODE_TEXTURE_SIZE else FULL_MODE_TEXTURE_SIZE
        context.graphics.blit(
            RenderPipelines.GUI_TEXTURED,
            modeTexture,
            controls.mode.x,
            controls.mode.y,
            0f,
            0f,
            BUTTON_SIZE,
            BUTTON_SIZE,
            modeTextureSize,
            modeTextureSize,
            modeTextureSize,
            modeTextureSize
        )

        renderArrowButton(
            context,
            controls.right,
            if (rightHovered && canGoRight) arrowRightSelected else arrowRight,
            canGoRight
        )

        if (state.layered) {
            val label = "${state.layer + 1}/${multiblock.ySize.coerceAtLeast(1)}"
            val font = Minecraft.getInstance().font
            val labelWidth = font.width(label) / 2f
            context.graphics.pose().pushMatrix()
            context.graphics.pose().translate(
                controls.mode.x + BUTTON_SIZE / 2f - labelWidth / 2f,
                controls.mode.y - 5f
            )
            context.graphics.pose().scale(0.5f, 0.5f)
            context.graphics.text(font, label, 0, 0, 0xFF404040.toInt(), false)
            context.graphics.pose().popMatrix()
        }
    }

    private fun renderArrowButton(
        context: BookElementRenderContext,
        bounds: Rect,
        texture: Identifier,
        enabled: Boolean
    ) {
        context.graphics.blit(
            RenderPipelines.GUI_TEXTURED,
            texture,
            bounds.x,
            bounds.y,
            0f,
            0f,
            BUTTON_SIZE,
            BUTTON_SIZE,
            ARROW_TEXTURE_WIDTH,
            ARROW_TEXTURE_HEIGHT,
            ARROW_TEXTURE_WIDTH,
            ARROW_TEXTURE_HEIGHT,
            if (enabled) 0xFFFFFFFF.toInt() else DISABLED_ARROW_TINT
        )
    }

    fun mouseClicked(mouseX: Int, mouseY: Int, button: Int, shift: Boolean): Boolean {
        if (button != LEFT_MOUSE_BUTTON && button != RIGHT_MOUSE_BUTTON) return false
        val entry = visibleStates().lastOrNull { (_, state) ->
            (button == LEFT_MOUSE_BUTTON && (
                state.leftButton.contains(mouseX, mouseY, BUTTON_HIT_PADDING) ||
                    state.modeButton.contains(mouseX, mouseY, BUTTON_HIT_PADDING) ||
                    state.rightButton.contains(mouseX, mouseY, BUTTON_HIT_PADDING)
                )) || state.previewBounds.contains(mouseX, mouseY)
        } ?: return false

        val (key, state) = entry
        when {
            button == LEFT_MOUSE_BUTTON && state.leftButton.contains(mouseX, mouseY, BUTTON_HIT_PADDING) -> {
                if (state.layered) state.layer = (state.layer - 1).coerceAtLeast(0)
            }
            button == LEFT_MOUSE_BUTTON && state.modeButton.contains(mouseX, mouseY, BUTTON_HIT_PADDING) -> {
                state.layered = !state.layered
            }
            button == LEFT_MOUSE_BUTTON && state.rightButton.contains(mouseX, mouseY, BUTTON_HIT_PADDING) -> {
                if (state.layered) state.layer = (state.layer + 1).coerceAtMost(state.maxLayer)
            }
            state.previewBounds.contains(mouseX, mouseY) -> {
                state.dragMode = if (button == RIGHT_MOUSE_BUTTON || shift) DragMode.PAN else DragMode.ROTATE
                draggingKey = key
                draggingButton = button
            }
        }
        return true
    }

    fun mouseDragged(dragX: Double, dragY: Double, shift: Boolean): Boolean {
        val state = draggingKey?.let(states::get) ?: return false
        val mode = if (draggingButton == RIGHT_MOUSE_BUTTON || shift) DragMode.PAN else state.dragMode
        when (mode) {
            DragMode.PAN -> {
                state.offsetX += dragX.toFloat()
                state.offsetY += dragY.toFloat()
            }
            DragMode.ROTATE -> {
                state.rotationX += dragX.toFloat() * ROTATION_SPEED
                state.rotationY = (state.rotationY - dragY.toFloat() * ROTATION_SPEED)
                    .coerceIn(MIN_VERTICAL_ROTATION, MAX_VERTICAL_ROTATION)
            }
        }
        return true
    }

    fun mouseReleased(button: Int): Boolean {
        if (draggingKey == null || draggingButton != button) return false
        draggingKey = null
        draggingButton = null
        return true
    }

    fun mouseScrolled(mouseX: Int, mouseY: Int, scrollY: Double): Boolean {
        val state = visibleStates().lastOrNull { (_, state) -> state.previewBounds.contains(mouseX, mouseY) }
            ?.value
            ?: return false
        val multiplier = exp(scrollY.toFloat() * ZOOM_STEP)
        state.scale = (state.scale * multiplier).coerceIn(MIN_SCALE, MAX_SCALE)
        return true
    }

    private fun visibleStates(): List<Map.Entry<String, PreviewState>> = states.entries
        .filter { it.value.lastSeenFrame == frame }

    private fun controlLayout(context: BookElementRenderContext): Controls {
        val totalWidth = BUTTON_SIZE * 3 + BUTTON_GAP * 2
        val startX = context.x + (context.width - totalWidth) / 2
        val y = context.y + context.height - BUTTON_SIZE - CONTROL_BOTTOM_MARGIN
        return Controls(
            Rect(startX, y, BUTTON_SIZE, BUTTON_SIZE),
            Rect(startX + BUTTON_SIZE + BUTTON_GAP, y, BUTTON_SIZE, BUTTON_SIZE),
            Rect(startX + (BUTTON_SIZE + BUTTON_GAP) * 2, y, BUTTON_SIZE, BUTTON_SIZE)
        )
    }

    private fun Rect.toScreen(context: BookElementRenderContext): Rect {
        val relativeX = x - context.x
        val relativeY = y - context.y
        return Rect(
            context.screenX + (relativeX * context.scale).roundToInt(),
            context.screenY + (relativeY * context.scale).roundToInt(),
            max(1, (width * context.scale).roundToInt()),
            max(1, (height * context.scale).roundToInt())
        )
    }

    private fun shiftDown(): Boolean {
        val window = Minecraft.getInstance().window
        return InputConstants.isKeyDown(window, InputConstants.KEY_LSHIFT) ||
            InputConstants.isKeyDown(window, InputConstants.KEY_RSHIFT)
    }

    private data class Controls(val left: Rect, val mode: Rect, val right: Rect)

    private data class Rect(val x: Int, val y: Int, val width: Int, val height: Int) {
        fun contains(mouseX: Int, mouseY: Int, padding: Int = 0): Boolean =
            mouseX >= x - padding && mouseX < x + width + padding &&
                mouseY >= y - padding && mouseY < y + height + padding
    }

    private enum class DragMode { ROTATE, PAN }

    private class PreviewState(
        var scale: Float,
        var rotationX: Float,
        var rotationY: Float,
        var offsetX: Float,
        var offsetY: Float,
        var layer: Int,
        var layered: Boolean
    ) {
        var previewBounds = Rect(0, 0, 0, 0)
        var leftButton = Rect(0, 0, 0, 0)
        var modeButton = Rect(0, 0, 0, 0)
        var rightButton = Rect(0, 0, 0, 0)
        var dragMode = DragMode.ROTATE
        var maxLayer = 0
        var lastSeenFrame = -1L

        fun transform(): MultiblockPreviewTransform = MultiblockPreviewTransform(
            scale = scale,
            rotationX = rotationX,
            rotationY = rotationY,
            offsetX = offsetX,
            offsetY = offsetY,
            layer = if (layered) layer else Int.MAX_VALUE,
            singleLayer = layered
        )

        companion object {
            fun from(element: MultiblockBookElement, multiblock: Multiblock): PreviewState {
                val layered = element.layer != Int.MAX_VALUE
                return PreviewState(
                    scale = element.scale.coerceIn(MIN_SCALE, MAX_SCALE),
                    rotationX = element.rotationX,
                    rotationY = element.rotationY.coerceIn(MIN_VERTICAL_ROTATION, MAX_VERTICAL_ROTATION),
                    offsetX = 0f,
                    offsetY = 0f,
                    layer = if (layered) element.layer.coerceIn(0, multiblock.ySize - 1) else 0,
                    layered = layered
                )
            }
        }
    }

    private const val BUTTON_SIZE = 8
    private const val BUTTON_GAP = 2
    private const val BUTTON_HIT_PADDING = 2
    private const val CONTROL_BOTTOM_MARGIN = 1
    private const val CONTROL_RESERVED_HEIGHT = 14
    private const val ARROW_TEXTURE_WIDTH = 27
    private const val ARROW_TEXTURE_HEIGHT = 23
    private const val FULL_MODE_TEXTURE_SIZE = 16
    private const val LAYERED_MODE_TEXTURE_SIZE = 8
    private const val DISABLED_ARROW_TINT = 0x66FFFFFF
    private const val LEFT_MOUSE_BUTTON = 0
    private const val RIGHT_MOUSE_BUTTON = 1
    private const val ROTATION_SPEED = 0.75f
    private const val ZOOM_STEP = 0.12f
    private const val MIN_SCALE = 0.25f
    private const val MAX_SCALE = 3.0f
    private const val MIN_VERTICAL_ROTATION = -89f
    private const val MAX_VERTICAL_ROTATION = 89f
}
