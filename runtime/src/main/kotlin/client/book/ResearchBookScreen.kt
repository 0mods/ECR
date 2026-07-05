package com.algorithmlx.ecr.client.book

import com.algorithmlx.ecr.api.ModId
import com.algorithmlx.ecr.api.client.research.BookElementRenderContext
import com.algorithmlx.ecr.api.client.research.BookElementRenderers
import com.algorithmlx.ecr.api.ecRL
import com.algorithmlx.ecr.api.research.*
import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.input.KeyEvent
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.util.Mth
import net.minecraft.world.item.ItemStack
import java.awt.Color
import kotlin.math.hypot
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sin

class ResearchBookScreen : Screen(Component.translatable("screen.$ModId.research_book")) {
    private val bookTexture = "textures/gui/book/book.png".ecRL
    private val bookmarkTexture = "textures/gui/book/bookmark.png".ecRL
    private val bookmarkSelectedTexture = "textures/gui/book/bookmark_selected.png".ecRL
    private val arrowLeft = "textures/gui/book/arrow_left.png".ecRL
    private val arrowLeftSelected = "textures/gui/book/arrow_left_selected.png".ecRL
    private val arrowRight = "textures/gui/book/arrow_right.png".ecRL
    private val arrowRightSelected = "textures/gui/book/arrow_right_selected.png".ecRL

    private var selectedCategory: Identifier? = null
    private var selectedEntry: BookEntry? = null
    private var spreads = listOf(BookSpread(emptyList()))
    private var spreadIndex = 0

    private var panX = 0f
    private var panY = 0f
    private var targetPanX = 0f
    private var targetPanY = 0f

    private var zoom = 1f
    private var targetZoom = 1f

    private var categoryScroll = 0f
    private var targetCategoryScroll = 0f

    private var bookmarkHoverProgress = 0f
    private val globalBookmarkProgress = mutableMapOf<Identifier, Float>()

    private var draggingGraph = false
    private var draggingCategorySlider = false
    private var partialTick = 0f

    private var showColorPicker = false
    private var pickerHue = 0f
    private var pickerSat = 0f
    private var pickerVal = 1f
    private var draggingHue = false
    private var draggingSat = false

    private var lastFrameNanos = -1L
    private var frameDt = 0f

    init {
        BookDefaultRenderers.init()
    }

    override fun init() {
        super.init()
        val categories = categories()
        val saved = ClientResearchState.viewState()
        val savedCategory = saved.category?.takeIf { id -> categories.any { it.id == id && ClientResearchState.categoryAvailable(it) } }
        selectedCategory = savedCategory ?: categories.firstOrNull(ClientResearchState::categoryAvailable)?.id
        if (savedCategory != null) {
            targetPanX = saved.panX
            targetPanY = saved.panY
            targetZoom = saved.zoom.coerceIn(0.5f, 2f)
            saved.entry
                ?.takeIf(ClientResearchState::has)
                ?.let { ResearchCatalog.snapshot().entries[it] }
                ?.takeIf { ResearchCatalog.snapshot().layout[it.id]?.category == selectedCategory }
                ?.let { openEntry(it, saved.spread) }
        } else {
            centerCategory()
        }
        constrainPan()
        panX = targetPanX
        panY = targetPanY
        zoom = targetZoom
    }

    override fun extractRenderState(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, partialTick: Float) {
        this.partialTick = partialTick
        this.frameDt = frameDelta()

        panX = approach(panX, targetPanX, frameDt, PAN_HALF_LIFE)
        panY = approach(panY, targetPanY, frameDt, PAN_HALF_LIFE)
        zoom = approach(zoom, targetZoom, frameDt, ZOOM_HALF_LIFE)
        categoryScroll = approach(categoryScroll, targetCategoryScroll, frameDt, SCROLL_HALF_LIFE)
        constrainPan()

        val favorites = ResearchCatalog.snapshot().entries.values.filter { ClientResearchState.favoriteColor(it.id) != null }
        favorites.forEachIndexed { index, entry ->
            val bX = width - 24 - index * 20
            val current = globalBookmarkProgress[entry.id] ?: 0f
            val currentYOffset = Mth.lerp(current, -24f, 0f).toInt()
            val isHovered = mouseX in bX..(bX + 16) && mouseY in CATEGORY_HEIGHT..(48 + currentYOffset)

            globalBookmarkProgress[entry.id] = approach(current, if (isHovered) 1f else 0f, frameDt, BOOKMARK_HOVER_HALF_LIFE)
        }

        if (selectedEntry == null) {
            renderSpace(graphics)
            renderGraph(graphics, mouseX, mouseY)
        } else {
            graphics.fill(0, 0, width, height, 0xFF080A10.toInt())
            renderBook(graphics, mouseX, mouseY, partialTick)
        }
    }

    override fun mouseClicked(event: MouseButtonEvent, doubleClick: Boolean): Boolean {
        if (event.button() != 0) return super.mouseClicked(event, doubleClick)
        val mouseX = event.x().toInt()
        val mouseY = event.y().toInt()

        if (selectedEntry != null) return handleBookClick(mouseX, mouseY)
        if (selectGlobalBookmarkAt(mouseX, mouseY)) return true
        if (selectCategoryAt(mouseX, mouseY)) return true
        if (hasCategoryOverflow() && mouseY in 14..20) {
            draggingCategorySlider = true
            updateCategorySlider(mouseX)
            return true
        }

        nodeAt(mouseX, mouseY)?.let { node ->
            if (!isAvailable(node.entry)) return true
            openEntry(node.entry)
            return true
        }

        if (mouseY >= GRAPH_TOP) {
            draggingGraph = true
            return true
        }
        return super.mouseClicked(event, doubleClick)
    }

    override fun mouseDragged(event: MouseButtonEvent, dragX: Double, dragY: Double): Boolean {
        if (event.button() != 0) return super.mouseDragged(event, dragX, dragY)

        if ((draggingSat || draggingHue) && selectedEntry != null) {
            val transform = bookTransform()
            val x = ((event.x() - transform.x) / transform.scale).toInt()
            val y = ((event.y() - transform.y) / transform.scale).toInt()
            val (px, py) = pickerBounds()
            val svX = px + PICKER_PADDING
            val svY = py + PICKER_PADDING
            if (draggingSat) updateSatVal(x - svX, y - svY)
            if (draggingHue) updateHue(y - svY)
            return true
        }

        if (draggingCategorySlider) {
            updateCategorySlider(event.x().toInt())
            return true
        }
        if (draggingGraph && selectedEntry == null) {
            val dx = (dragX / zoom).toFloat()
            val dy = (dragY / zoom).toFloat()
            targetPanX += dx
            targetPanY += dy
            return true
        }
        return super.mouseDragged(event, dragX, dragY)
    }

    override fun mouseReleased(event: MouseButtonEvent): Boolean {
        draggingGraph = false
        draggingCategorySlider = false
        draggingHue = false
        draggingSat = false
        return super.mouseReleased(event)
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, scrollX: Double, scrollY: Double): Boolean {
        if (selectedEntry != null) return false
        val window = Minecraft.getInstance().window
        val shift = InputConstants.isKeyDown(window, InputConstants.KEY_LSHIFT) ||
                InputConstants.isKeyDown(window, InputConstants.KEY_RSHIFT)

        if (shift && hasCategoryOverflow()) {
            targetCategoryScroll = (targetCategoryScroll - scrollY.toFloat() * 24f).coerceIn(0f, maxCategoryScroll())
            return true
        }

        val oldTargetZoom = targetZoom
        targetZoom = (targetZoom + scrollY.toFloat() * 0.12f).coerceIn(0.5f, 2f)
        val graphX = mouseX.toFloat() - width / 2f
        val graphY = mouseY.toFloat() - GRAPH_TOP

        if (oldTargetZoom != targetZoom) {
            targetPanX -= graphX * (1f / oldTargetZoom - 1f / targetZoom)
            targetPanY -= graphY * (1f / oldTargetZoom - 1f / targetZoom)
        }
        return true
    }

    override fun keyPressed(event: KeyEvent): Boolean {
        if (event.key() == InputConstants.KEY_ESCAPE) {
            if (showColorPicker) {
                showColorPicker = false
                return true
            }
            if (selectedEntry != null) {
                closeEntry()
                return true
            }
        }
        return super.keyPressed(event)
    }

    override fun isPauseScreen(): Boolean = false

    override fun removed() {
        saveViewState()
        super.removed()
    }

    private fun frameDelta(): Float {
        val now = System.nanoTime()
        val delta = if (lastFrameNanos < 0) 0f else ((now - lastFrameNanos) / 1_000_000_000f).coerceIn(0f, 0.1f)
        lastFrameNanos = now
        return delta
    }

    private fun approach(current: Float, target: Float, dt: Float, halfLife: Float): Float {
        if (halfLife <= 0f || dt <= 0f) return target
        val decay = 0.5f.pow(dt / halfLife)
        val result = target + (current - target) * decay
        return if (kotlin.math.abs(result - target) < 0.01f) target else result
    }

    private fun renderSpace(graphics: GuiGraphicsExtractor) {
        val encodedX = encodeOffset(panX)
        val encodedY = encodeOffset(panY)
        val red = encodedX ushr OFFSET_LOW_BITS
        val green = encodedY ushr OFFSET_LOW_BITS
        val lowY = encodedY and OFFSET_LOW_MASK
        val zoomBits = (((zoom - 0.5f) / 1.5f).coerceIn(0f, 1f) * ZOOM_MAX).roundToInt()
        val blue = ((lowY ushr 3) shl 7) or zoomBits
        val alpha = 0x80 or ((encodedX and OFFSET_LOW_MASK) shl 3) or (lowY and 0x7)
        val color = (alpha shl 24) or (red shl 16) or (green shl 8) or blue

        graphics.fill(BookRenderPipelines.forCategory(selectedCategory()), 0, 0, width, height, color)
    }

    private fun renderGraph(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int) {
        renderGlobalBookmarks(graphics, mouseX, mouseY)
        renderCategories(graphics, mouseX, mouseY)
        graphics.enableScissor(0, GRAPH_TOP, width, height)
        val nodes = visibleNodes()
        nodes.forEach { node ->
            node.entry.dependencies.forEach { dependencyId ->
                val dependency = ResearchCatalog.snapshot().layout[dependencyId]
                if (dependency != null && dependency.category == node.category) renderThread(graphics, dependency, node)
            }
        }
        nodes.forEach { renderNode(graphics, it, mouseX, mouseY) }
        graphics.disableScissor()
    }

    private fun renderGlobalBookmarks(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int) {
        graphics.enableScissor(0, CATEGORY_HEIGHT, width, height)

        val favorites = ResearchCatalog.snapshot().entries.values.filter { ClientResearchState.favoriteColor(it.id) != null }
        favorites.forEachIndexed { index, entry ->
            val argb = ClientResearchState.favoriteColor(entry.id) ?: return@forEachIndexed
            val color = 0xFF000000.toInt() or (argb and 0xFFFFFF)
            val bX = width - 24 - index * 20
            val bY = 0

            val progress = globalBookmarkProgress[entry.id] ?: 0f
            val yOffset = Mth.lerp(progress, -24f, 0f).toInt()

            graphics.pose().pushMatrix()
            graphics.pose().translate((bX + 16).toFloat(), (bY + yOffset).toFloat())
            graphics.pose().rotate(Math.toRadians(90.0).toFloat())
            graphics.blit(RenderPipelines.GUI_TEXTURED, bookmarkSelectedTexture, 0, 0, 0f, 0f, 48, 16, 48, 16, color)
            graphics.pose().popMatrix()

            if (mouseX in bX..(bX + 16) && mouseY in CATEGORY_HEIGHT..(48 + yOffset)) {
                graphics.setTooltipForNextFrame(entry.title.component(entry.titleShadow), mouseX, mouseY)
            }
        }

        graphics.disableScissor()
    }

    private fun renderCategories(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int) {
        categories().forEachIndexed { index, category ->
            val x = (index * TAB_WIDTH - categoryScroll).toInt()
            if (x + TAB_WIDTH < 0 || x > width) return@forEachIndexed
            val selected = category.id == selectedCategory
            val available = ClientResearchState.categoryAvailable(category)

            val bgColor = if (selected) 0x40FFFFFF else 0x00000000
            if (bgColor != 0) {
                graphics.fill(x, 0, x + TAB_WIDTH, CATEGORY_HEIGHT, bgColor)
            }

            renderIcon(graphics, category.icon, x + 4, 3, 10)
            if (!available) graphics.fill(x, 0, x + TAB_WIDTH, CATEGORY_HEIGHT, 0xA0000000.toInt())

            if (mouseX in x until x + TAB_WIDTH && mouseY in 0..CATEGORY_HEIGHT) {
                graphics.setTooltipForNextFrame(category.title.component(category.titleShadow), mouseX, mouseY)
            }
        }

        if (hasCategoryOverflow()) {
            val trackWidth = width.coerceAtLeast(1)
            val thumbWidth = (trackWidth * (trackWidth.toFloat() / (categories().size * TAB_WIDTH))).toInt().coerceAtLeast(24)
            val thumbX = ((trackWidth - thumbWidth) * (categoryScroll / maxCategoryScroll())).toInt()
            graphics.fill(0, 16, trackWidth, 18, 0x80101820.toInt())
            graphics.fill(thumbX, 16, thumbX + thumbWidth, 18, 0xFFD0D8E8.toInt())
        }
    }

    private fun renderThread(graphics: GuiGraphicsExtractor, from: ResolvedBookEntry, to: ResolvedBookEntry) {
        val fromPoint = nodeCenter(from)
        val toPoint = nodeCenter(to)
        val dx = (toPoint.first - fromPoint.first).toFloat()
        val dy = (toPoint.second - fromPoint.second).toFloat()
        val distance = hypot(dx.toDouble(), dy.toDouble()).toFloat()
        if (distance < 1f) return

        val normalX = -dy / distance
        val normalY = dx / distance
        val completed = ClientResearchState.has(to.entry.id)
        val strands = if (completed) 1 else 3
        val steps = (distance / if (completed) 1.5f else 2.25f).roundToInt().coerceAtLeast(1)
        val time = System.nanoTime() / 1_000_000_000f

        repeat(strands) { strand ->
            for (step in 0..steps) {
                if (!completed && (step + (time * 10f).toInt() + strand * 3) % 10 >= 6) continue
                val progress = step.toFloat() / steps
                val envelope = sin(Math.PI.toFloat() * progress)
                val wave = if (completed) {
                    sin(progress * Math.PI.toFloat() * 2f + time * 1.4f) * 0.35f * envelope
                } else {
                    val spread = (strand - 1) * 1.35f
                    val frequency = Math.PI.toFloat() * (4.5f + strand)
                    (spread + sin(progress * frequency + time * (1.6f + strand * 0.18f) + strand * 2.1f) * (2.1f + strand * 0.45f)) * envelope
                }
                val x = Mth.lerp(progress, fromPoint.first.toFloat(), toPoint.first.toFloat()) + normalX * wave
                val y = Mth.lerp(progress, fromPoint.second.toFloat(), toPoint.second.toFloat()) + normalY * wave
                val color = if (completed) 0xFFEAF8FF.toInt() else THREAD_COLORS[strand]
                graphics.fill(BookRenderPipelines.THREAD, x.toInt(), y.toInt(), x.toInt() + 2, y.toInt() + 2, color)
            }
        }
    }

    private fun renderNode(graphics: GuiGraphicsExtractor, node: ResolvedBookEntry, mouseX: Int, mouseY: Int) {
        val available = isAvailable(node.entry)
        if (node.entry.hiddenUntilAvailable && !available) return

        val point = nodePoint(node)
        val frame = node.entry.frame

        graphics.pose().pushMatrix()
        graphics.pose().translate(point.first, point.second)
        graphics.pose().scale(zoom, zoom)

        if (frame != null) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, frame.texture, 0, 0, 0f, 0f, frame.width, frame.height, frame.width, frame.height)
        }

        val iconX = frame?.itemX ?: 0
        val iconY = frame?.itemY ?: 0
        renderIcon(graphics, node.entry.icon, iconX, iconY, frame?.itemSize ?: 16)

        if (!available || !ClientResearchState.has(node.entry.id)) {
            graphics.fill(0, 0, nodeWidth(node.entry), nodeHeight(node.entry), 0x78000000)
        }
        graphics.pose().popMatrix()

        if (isInsideNode(node, mouseX, mouseY)) {
            val tooltip = mutableListOf(node.entry.title.component(node.entry.titleShadow))
            ClientResearchState.taskProgress(node.entry.id).forEach { tooltip += Component.literal("${it.current}/${it.required}") }
            graphics.setComponentTooltipForNextFrame(Minecraft.getInstance().font, tooltip, mouseX, mouseY)
        }
    }

    private fun renderBook(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, partialTick: Float) {
        val entry = selectedEntry ?: return
        val transform = bookTransform()
        val localMouseX = ((mouseX - transform.x) / transform.scale).toInt()
        val localMouseY = ((mouseY - transform.y) / transform.scale).toInt()

        graphics.pose().pushMatrix()
        graphics.pose().translate(transform.x.toFloat(), transform.y.toFloat())
        graphics.pose().scale(transform.scale, transform.scale)

        renderBookmark(graphics, entry)
        graphics.blit(RenderPipelines.GUI_TEXTURED, bookTexture, 0, 0, 0f, 0f, BOOK_WIDTH, BOOK_HEIGHT, BOOK_WIDTH, BOOK_HEIGHT)

        val isBookmarkHovered = localMouseX in 496..<550 && localMouseY in BOOKMARK_Y until BOOKMARK_Y + 16
        val targetHover = if (isBookmarkHovered) 1f else 0f
        bookmarkHoverProgress = approach(bookmarkHoverProgress, targetHover, frameDt, BOOKMARK_HOVER_HALF_LIFE)

        renderPageArrows(graphics, localMouseX, localMouseY)
        renderColorPicker(graphics)

        graphics.enableScissor(0, 0, BOOK_WIDTH, BOOK_HEIGHT)

        spreads[spreadIndex].elements.forEach { placement ->
            val absX = transform.x + (placement.x * transform.scale).toInt()
            val absY = transform.y + (placement.y * transform.scale).toInt()
            val absW = (placement.width * transform.scale).toInt()
            val absH = (placement.height * transform.scale).toInt()

            BookElementRenderers.render(
                placement.element.content.type,
                BookElementRenderContext(
                    graphics,
                    placement.x,
                    placement.y,
                    placement.width,
                    placement.height,
                    localMouseX,
                    localMouseY,
                    partialTick,
                    absX,
                    absY,
                    absW,
                    absH,
                    transform.scale,
                    placement.textLines
                ),
                placement.element.content
            )
        }
        graphics.disableScissor()

        val font = Minecraft.getInstance().font
        val title = entry.title.component(entry.titleShadow)
        graphics.text(font, title, BOOK_WIDTH / 2 - font.width(title) / 2, 8, 0xFF263746.toInt(), entry.titleShadow)
        renderCompleteButton(graphics, entry, localMouseX, localMouseY)

        graphics.pose().popMatrix()
    }

    private fun bookTransform(): BookTransform {
        val finalScale = min((width - 40f) / BOOK_WIDTH, (height - 40f) / BOOK_HEIGHT).coerceAtMost(1f)
        return BookTransform(
            ((width - BOOK_WIDTH * finalScale) / 2f).toInt(),
            ((height - BOOK_HEIGHT * finalScale) / 2f).toInt(),
            finalScale
        )
    }

    private fun renderBookmark(graphics: GuiGraphicsExtractor, entry: BookEntry) {
        val argb = ClientResearchState.favoriteColor(entry.id)
        val color = if (argb != null) (0xFF000000.toInt() or (argb and 0xFFFFFF)) else null
        val texture = if (color == null) bookmarkTexture else bookmarkSelectedTexture

        val offset = Mth.lerp(bookmarkHoverProgress, -6f, 18f).toInt()

        graphics.blit(
            RenderPipelines.GUI_TEXTURED,
            texture,
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

    private fun renderPageArrows(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int) {
        if (spreadIndex > 0) {
            val leftHovered = mouseX in LEFT_ARROW_X until LEFT_ARROW_X + 27 && mouseY in ARROW_Y until ARROW_Y + 23
            graphics.blit(RenderPipelines.GUI_TEXTURED, if (leftHovered) arrowLeftSelected else arrowLeft, LEFT_ARROW_X, ARROW_Y, 0f, 0f, 27, 23, 27, 23)
        }
        if (spreadIndex < spreads.lastIndex) {
            val rightHovered = mouseX in RIGHT_ARROW_X until RIGHT_ARROW_X + 27 && mouseY in ARROW_Y until ARROW_Y + 23
            graphics.blit(RenderPipelines.GUI_TEXTURED, if (rightHovered) arrowRightSelected else arrowRight, RIGHT_ARROW_X, ARROW_Y, 0f, 0f, 27, 23, 27, 23)
        }
    }

    private fun handleBookClick(mouseX: Int, mouseY: Int): Boolean {
        val entry = selectedEntry ?: return false
        val transform = bookTransform()
        val x = ((mouseX - transform.x) / transform.scale).toInt()
        val y = ((mouseY - transform.y) / transform.scale).toInt()

        if (showColorPicker) {
            handlePickerClick(x, y)
            return true
        }

        if (shouldShowCompleteButton(entry) && x in COMPLETE_BUTTON_X until COMPLETE_BUTTON_X + COMPLETE_BUTTON_WIDTH &&
            y in COMPLETE_BUTTON_Y until COMPLETE_BUTTON_Y + COMPLETE_BUTTON_HEIGHT
        ) {
            if (tasksComplete(entry)) ResearchNetwork.completeResearch(entry.id)
            return true
        }

        if (x in 496..<550 && y in BOOKMARK_Y until BOOKMARK_Y + 16) {
            openColorPicker(entry)
            return true
        }

        if (spreadIndex > 0 && x in LEFT_ARROW_X until LEFT_ARROW_X + 27 && y in ARROW_Y until ARROW_Y + 23) {
            spreadIndex--
            return true
        }

        if (spreadIndex < spreads.lastIndex && x in RIGHT_ARROW_X until RIGHT_ARROW_X + 27 && y in ARROW_Y until ARROW_Y + 23) {
            spreadIndex++
            return true
        }

        return true
    }

    private fun renderCompleteButton(graphics: GuiGraphicsExtractor, entry: BookEntry, mouseX: Int, mouseY: Int) {
        if (!shouldShowCompleteButton(entry)) return
        val enabled = tasksComplete(entry)
        val hovered = enabled && mouseX in COMPLETE_BUTTON_X until COMPLETE_BUTTON_X + COMPLETE_BUTTON_WIDTH &&
            mouseY in COMPLETE_BUTTON_Y until COMPLETE_BUTTON_Y + COMPLETE_BUTTON_HEIGHT
        val background = when {
            !enabled -> 0xFF343840.toInt()
            hovered -> 0xFF55789A.toInt()
            else -> 0xFF3F607E.toInt()
        }
        val border = if (enabled) 0xFFB7D3EA.toInt() else 0xFF686D76.toInt()
        val textColor = if (enabled) 0xFFFFFFFF.toInt() else 0xFF9A9DA3.toInt()
        graphics.fill(
            COMPLETE_BUTTON_X,
            COMPLETE_BUTTON_Y,
            COMPLETE_BUTTON_X + COMPLETE_BUTTON_WIDTH,
            COMPLETE_BUTTON_Y + COMPLETE_BUTTON_HEIGHT,
            background
        )
        graphics.outline(
            COMPLETE_BUTTON_X,
            COMPLETE_BUTTON_Y,
            COMPLETE_BUTTON_WIDTH,
            COMPLETE_BUTTON_HEIGHT,
            border
        )
        val font = Minecraft.getInstance().font
        val label = Component.translatable("screen.$ModId.complete_research")
        val textX = COMPLETE_BUTTON_X + (COMPLETE_BUTTON_WIDTH - font.width(label)) / 2
        val textY = COMPLETE_BUTTON_Y + (COMPLETE_BUTTON_HEIGHT - font.lineHeight) / 2
        graphics.text(font, label, textX, textY, textColor, false)
    }

    private fun shouldShowCompleteButton(entry: BookEntry): Boolean =
        !entry.automatic && !ClientResearchState.has(entry.id) && isAvailable(entry)

    private fun tasksComplete(entry: BookEntry): Boolean {
        if (entry.tasks.isEmpty()) return true
        val progress = ClientResearchState.taskProgress(entry.id)
        return progress.size == entry.tasks.size && progress.all(ResearchTaskProgress::complete)
    }

    private fun openColorPicker(entry: BookEntry) {
        val current = ClientResearchState.favoriteColor(entry.id)
        if (current != null) {
            val hsb = FloatArray(3)
            Color.RGBtoHSB((current shr 16) and 0xFF, (current shr 8) and 0xFF, current and 0xFF, hsb)
            pickerHue = hsb[0]
            pickerSat = hsb[1]
            pickerVal = hsb[2]
        } else {
            pickerHue = 0f
            pickerSat = 0f
            pickerVal = 1f
        }
        showColorPicker = true
    }

    private fun pickerBounds(): Pair<Int, Int> {
        val px = (BOOKMARK_X - PICKER_WIDTH - 10).coerceAtLeast(4)
        val py = (BOOKMARK_Y + 4).coerceIn(4, (BOOK_HEIGHT - PICKER_HEIGHT - 4).coerceAtLeast(4))
        return px to py
    }

    private fun updateSatVal(localX: Int, localY: Int) {
        pickerSat = (localX.toFloat() / SV_SIZE).coerceIn(0f, 1f)
        pickerVal = 1f - (localY.toFloat() / SV_SIZE).coerceIn(0f, 1f)
        applyPickerColor()
    }

    private fun updateHue(localY: Int) {
        pickerHue = (localY.toFloat() / HUE_STRIP_HEIGHT).coerceIn(0f, 1f)
        applyPickerColor()
    }

    private fun applyPickerColor() {
        val entry = selectedEntry ?: return
        val rgb = Color.HSBtoRGB(pickerHue, pickerSat, pickerVal)
        val alpha = spreadIndex and 0xFF
        val argb = (alpha shl 24) or (rgb and 0xFFFFFF)
        ResearchNetwork.updateFavorite(entry.id, argb)
    }

    private fun handlePickerClick(x: Int, y: Int) {
        val entry = selectedEntry ?: run { showColorPicker = false; return }
        val (px, py) = pickerBounds()
        val svX = px + PICKER_PADDING
        val svY = py + PICKER_PADDING
        val hueX = svX + SV_SIZE + PICKER_PADDING
        val hueY = svY
        val clearX = hueX
        val clearY = hueY + HUE_STRIP_HEIGHT + PICKER_PADDING

        when {
            x in svX until svX + SV_SIZE && y in svY until svY + SV_SIZE -> {
                draggingSat = true
                updateSatVal(x - svX, y - svY)
            }
            x in hueX until hueX + HUE_STRIP_WIDTH && y in hueY until hueY + HUE_STRIP_HEIGHT -> {
                draggingHue = true
                updateHue(y - svY)
            }
            x in clearX until clearX + CLEAR_BTN_SIZE && y in clearY until clearY + CLEAR_BTN_SIZE -> {
                ResearchNetwork.updateFavorite(entry.id, null)
                showColorPicker = false
            }
            else -> {
                showColorPicker = false
            }
        }
    }

    private fun renderColorPicker(graphics: GuiGraphicsExtractor) {
        if (!showColorPicker) return
        val (px, py) = pickerBounds()

        graphics.fill(px - 3, py - 3, px + PICKER_WIDTH + 3, py + PICKER_HEIGHT + 3, 0xF0141820.toInt())
        graphics.fill(px - 3, py - 3, px + PICKER_WIDTH + 3, py - 2, 0xFF3A4656.toInt())
        graphics.fill(px - 3, py + PICKER_HEIGHT + 2, px + PICKER_WIDTH + 3, py + PICKER_HEIGHT + 3, 0xFF3A4656.toInt())

        val svX = px + PICKER_PADDING
        val svY = py + PICKER_PADDING

        val hueColor = 0xFF000000.toInt() or (Color.HSBtoRGB(pickerHue, 1f, 1f) and 0xFFFFFF)
        graphics.fill(svX, svY, svX + SV_SIZE, svY + SV_SIZE, hueColor)

        for (i in 0 until SV_SIZE) {
            val t = i.toFloat() / SV_SIZE
            val alpha = ((1f - t) * 255f).toInt().coerceIn(0, 255)
            val col = (alpha shl 24) or 0xFFFFFF
            graphics.fill(svX + i, svY, svX + i + 1, svY + SV_SIZE, col)
        }
        for (j in 0 until SV_SIZE) {
            val t = j.toFloat() / SV_SIZE
            val alpha = (t * 255f).toInt().coerceIn(0, 255)
            val col = alpha shl 24
            graphics.fill(svX, svY + j, svX + SV_SIZE, svY + j + 1, col)
        }

        val cursorX = svX + (pickerSat * SV_SIZE).toInt()
        val cursorY = svY + ((1f - pickerVal) * SV_SIZE).toInt()
        graphics.fill(cursorX - 2, cursorY - 2, cursorX + 3, cursorY + 3, 0xFFFFFFFF.toInt())
        graphics.fill(cursorX - 1, cursorY - 1, cursorX + 2, cursorY + 2, 0xFF000000.toInt())

        val hueX = svX + SV_SIZE + PICKER_PADDING
        val hueY = svY
        for (j in 0 until HUE_STRIP_HEIGHT) {
            val h = j.toFloat() / HUE_STRIP_HEIGHT
            val col = 0xFF000000.toInt() or (Color.HSBtoRGB(h, 1f, 1f) and 0xFFFFFF)
            graphics.fill(hueX, hueY + j, hueX + HUE_STRIP_WIDTH, hueY + j + 1, col)
        }
        val hueCursorY = (hueY + pickerHue * HUE_STRIP_HEIGHT).toInt()
        graphics.fill(hueX - 1, hueCursorY - 1, hueX + HUE_STRIP_WIDTH + 1, hueCursorY, 0xFFFFFFFF.toInt())
        graphics.fill(hueX - 1, hueCursorY + 1, hueX + HUE_STRIP_WIDTH + 1, hueCursorY + 2, 0xFFFFFFFF.toInt())

        val clearX = hueX
        val clearY = hueY + HUE_STRIP_HEIGHT + PICKER_PADDING
        graphics.fill(clearX, clearY, clearX + CLEAR_BTN_SIZE, clearY + CLEAR_BTN_SIZE, 0xFF2A323E.toInt())
        graphics.fill(clearX, clearY, clearX + CLEAR_BTN_SIZE, clearY + 1, 0xFFFF6B7A.toInt())
        graphics.fill(clearX, clearY + CLEAR_BTN_SIZE - 1, clearX + CLEAR_BTN_SIZE, clearY + CLEAR_BTN_SIZE, 0xFFFF6B7A.toInt())
        graphics.fill(clearX + CLEAR_BTN_SIZE - 1, clearY, clearX + CLEAR_BTN_SIZE, clearY + CLEAR_BTN_SIZE, 0xFFFF6B7A.toInt())
        graphics.fill(clearX, clearY, clearX + 1, clearY + CLEAR_BTN_SIZE, 0xFFFF6B7A.toInt())
    }

    private fun renderIcon(graphics: GuiGraphicsExtractor, icon: BookIcon, x: Int, y: Int, size: Int = 16) {
        icon.item?.let { id ->
            BuiltInRegistries.ITEM.getOptional(id).ifPresent {
                graphics.pose().pushMatrix()
                graphics.pose().translate(x.toFloat(), y.toFloat())
                val itemScale = size / 16f
                graphics.pose().scale(itemScale, itemScale)
                graphics.item(ItemStack(it), 0, 0)
                graphics.pose().popMatrix()
            }
        }
        icon.texture?.let { graphics.blit(RenderPipelines.GUI_TEXTURED, it, x, y, 0f, 0f, size, size, 16, 16) }
    }

    private fun selectGlobalBookmarkAt(mouseX: Int, mouseY: Int): Boolean {
        val favorites = ResearchCatalog.snapshot().entries.values.filter { ClientResearchState.favoriteColor(it.id) != null }
        favorites.forEachIndexed { index, entry ->
            val bX = width - 24 - index * 20
            val progress = globalBookmarkProgress[entry.id] ?: 0f
            val yOffset = Mth.lerp(progress, -24f, 0f).toInt()

            if (mouseX in bX..(bX + 16) && mouseY in CATEGORY_HEIGHT..(48 + yOffset)) {
                val argb = ClientResearchState.favoriteColor(entry.id) ?: return@forEachIndexed
                val page = (argb ushr 24) and 0xFF
                openEntry(entry, page)
                return true
            }
        }
        return false
    }

    private fun selectCategoryAt(mouseX: Int, mouseY: Int): Boolean {
        if (mouseY !in 0..16) return false
        categories().forEachIndexed { index, category ->
            val x = (index * TAB_WIDTH - categoryScroll).toInt()
            if (mouseX in x until x + TAB_WIDTH) {
                if (!ClientResearchState.categoryAvailable(category)) return true
                selectedCategory = category.id
                selectedEntry = null
                centerCategory()
                constrainPan()
                return true
            }
        }
        return false
    }

    private fun nodeAt(mouseX: Int, mouseY: Int): ResolvedBookEntry? =
        visibleNodes().lastOrNull { isInsideNode(it, mouseX, mouseY) }

    private fun isInsideNode(node: ResolvedBookEntry, mouseX: Int, mouseY: Int): Boolean {
        val point = nodePoint(node)
        val w = nodeWidth(node.entry) * zoom
        val h = nodeHeight(node.entry) * zoom
        return mouseX >= point.first && mouseX < point.first + w && mouseY >= point.second && mouseY < point.second + h
    }

    private fun nodePoint(node: ResolvedBookEntry): Pair<Float, Float> =
        width / 2f + (node.position.x + panX) * zoom to GRAPH_TOP + (node.position.y + panY) * zoom

    private fun nodeCenter(node: ResolvedBookEntry): Pair<Int, Int> {
        val point = nodePoint(node)
        return (point.first + nodeWidth(node.entry) * zoom / 2f).toInt() to
                (point.second + nodeHeight(node.entry) * zoom / 2f).toInt()
    }

    private fun visibleNodes(): List<ResolvedBookEntry> = selectedCategory?.let(ResearchCatalog.snapshot()::entriesIn).orEmpty()

    private fun isAvailable(entry: BookEntry): Boolean {
        val category = ResearchCatalog.snapshot().layout[entry.id]
            ?.category
            ?.let(ResearchCatalog.snapshot().categories::get)
            ?: return false
        return ClientResearchState.categoryAvailable(category) && entry.dependencies.all(ClientResearchState::has)
    }

    private fun openEntry(entry: BookEntry, page: Int = 0) {
        ResearchCatalog.snapshot().layout[entry.id]?.let { selectedCategory = it.category }
        selectedEntry = entry
        spreads = BookPageLayout.paginate(entry)
        spreadIndex = page.coerceIn(0, spreads.lastIndex)
        showColorPicker = false
    }

    private fun closeEntry() {
        selectedEntry = null
        spreads = listOf(BookSpread(emptyList()))
        spreadIndex = 0
        showColorPicker = false
    }

    private fun centerCategory() {
        val first = visibleNodes().minWithOrNull(compareBy<ResolvedBookEntry> { it.position.y }.thenBy { it.position.x }) ?: return
        targetPanX = -first.position.x.toFloat() - nodeWidth(first.entry) / 2f
        targetPanY = -first.position.y.toFloat() + 48f
        constrainPan()
    }

    private fun categories(): List<BookCategory> = ResearchCatalog.snapshot().categories.values
        .sortedWith(compareBy<BookCategory> { it.order }.thenBy { it.id.toString() })

    private fun hasCategoryOverflow(): Boolean = categories().size * TAB_WIDTH > width

    private fun maxCategoryScroll(): Float = (categories().size * TAB_WIDTH - width).coerceAtLeast(0).toFloat()

    private fun updateCategorySlider(mouseX: Int) {
        if (!hasCategoryOverflow()) return
        val thumbWidth = (width * (width.toFloat() / (categories().size * TAB_WIDTH))).toInt().coerceAtLeast(24)
        val ratio = ((mouseX - thumbWidth / 2f) / (width - thumbWidth).coerceAtLeast(1)).coerceIn(0f, 1f)
        targetCategoryScroll = ratio * maxCategoryScroll()
    }

    private fun encodeOffset(value: Float): Int {
        val normalized = value.coerceIn(-PARALLAX_PAN_RANGE, PARALLAX_PAN_RANGE) / PARALLAX_PAN_RANGE
        return ((normalized * 0.5f + 0.5f) * OFFSET_MAX).roundToInt()
    }

    private fun BookText.component(shadow: Boolean = false): Component {
        val component = if (translated) Component.translatable(value) else Component.literal(value)
        return if (shadow) component else component.withoutShadow()
    }

    private fun nodeWidth(entry: BookEntry) = entry.frame?.width ?: 18
    private fun nodeHeight(entry: BookEntry) = entry.frame?.height ?: 18

    private fun selectedCategory(): BookCategory? = selectedCategory?.let(ResearchCatalog.snapshot().categories::get)

    private fun constrainPan() {
        if (selectedEntry != null) return
        val nodes = visibleNodes()
        if (nodes.isEmpty()) return
        val safeZoom = targetZoom.coerceIn(0.5f, 2f)
        val minX = nodes.minOf { it.position.x.toFloat() }
        val maxX = nodes.maxOf { it.position.x + nodeWidth(it.entry).toFloat() }
        val minY = nodes.minOf { it.position.y.toFloat() }
        val maxY = nodes.maxOf { it.position.y + nodeHeight(it.entry).toFloat() }
        val minPanX = (SAFE_MARGIN - width / 2f) / safeZoom - maxX
        val maxPanX = (width - SAFE_MARGIN - width / 2f) / safeZoom - minX
        val minPanY = SAFE_MARGIN / safeZoom - maxY
        val maxPanY = (height - SAFE_MARGIN - GRAPH_TOP) / safeZoom - minY
        targetPanX = targetPanX.coerceIn(minPanX, maxPanX)
        targetPanY = targetPanY.coerceIn(minPanY, maxPanY)
    }

    private fun saveViewState() {
        val state = BookViewState(
            selectedCategory,
            selectedEntry?.id,
            spreadIndex,
            targetPanX,
            targetPanY,
            targetZoom
        )
        ClientResearchState.updateLocalView(state)
        ResearchNetwork.updateView(state)
    }

    private data class BookTransform(val x: Int, val y: Int, val scale: Float)

    companion object {
        private const val CATEGORY_HEIGHT = 16
        private const val GRAPH_TOP = 18
        private const val TAB_WIDTH = 18
        private const val BOOK_WIDTH = 512
        private const val BOOK_HEIGHT = 256

        private const val BOOKMARK_X = 484
        private const val BOOKMARK_Y = 16

        private const val LEFT_ARROW_X = 4
        private const val RIGHT_ARROW_X = 481
        private const val ARROW_Y = 116
        private const val COMPLETE_BUTTON_WIDTH = 140
        private const val COMPLETE_BUTTON_HEIGHT = 18
        private const val COMPLETE_BUTTON_X = (BOOK_WIDTH - COMPLETE_BUTTON_WIDTH) / 2
        private const val COMPLETE_BUTTON_Y = BOOK_HEIGHT + 2

        private const val PAN_HALF_LIFE = 0.06f
        private const val ZOOM_HALF_LIFE = 0.12f
        private const val SCROLL_HALF_LIFE = 0.12f
        private const val BOOKMARK_HOVER_HALF_LIFE = 0.08f
        private const val SAFE_MARGIN = 28f
        private const val PARALLAX_PAN_RANGE = 8192f
        private const val OFFSET_LOW_BITS = 4
        private const val OFFSET_LOW_MASK = (1 shl OFFSET_LOW_BITS) - 1
        private const val OFFSET_MAX = (1 shl 12) - 1
        private const val ZOOM_MAX = (1 shl 7) - 1
        private val THREAD_COLORS = intArrayOf(
            0xFF7184A4.toInt(),
            0xFF8B78A8.toInt(),
            0xFF657B96.toInt()
        )

        private const val PICKER_PADDING = 4
        private const val SV_SIZE = 46
        private const val HUE_STRIP_WIDTH = 10
        private const val HUE_STRIP_HEIGHT = 46
        private const val CLEAR_BTN_SIZE = 10
        private const val PICKER_WIDTH = PICKER_PADDING * 3 + SV_SIZE + HUE_STRIP_WIDTH
        private const val PICKER_HEIGHT = PICKER_PADDING * 2 + (HUE_STRIP_HEIGHT + PICKER_PADDING + CLEAR_BTN_SIZE)
    }
}
