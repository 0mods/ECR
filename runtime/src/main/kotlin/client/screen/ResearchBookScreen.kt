package com.algorithmlx.ecr.client.screen

import com.algorithmlx.ecr.api.ModId
import com.algorithmlx.ecr.api.client.research.BookElementRenderContext
import com.algorithmlx.ecr.api.client.research.BookElementRenderers
import com.algorithmlx.ecr.api.ecRL
import com.algorithmlx.ecr.api.research.BookCategory
import com.algorithmlx.ecr.api.research.BookEntry
import com.algorithmlx.ecr.api.research.BookIcon
import com.algorithmlx.ecr.api.research.BookText
import com.algorithmlx.ecr.api.research.BookViewState
import com.algorithmlx.ecr.api.research.ClientResearchState
import com.algorithmlx.ecr.api.research.ResearchCatalog
import com.algorithmlx.ecr.api.research.ResearchNetwork
import com.algorithmlx.ecr.api.research.ResearchTaskDefinition
import com.algorithmlx.ecr.api.research.ResearchTaskProgress
import com.algorithmlx.ecr.api.research.ResolvedBookEntry
import com.algorithmlx.ecr.client.book.BookBookmarkController
import com.algorithmlx.ecr.client.book.BookDefaultRenderers
import com.algorithmlx.ecr.client.book.BookPageLayout
import com.algorithmlx.ecr.client.book.BookRenderPipelines
import com.algorithmlx.ecr.client.book.BookSpread
import com.algorithmlx.ecr.client.book.BookThreadRenderer
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
import net.minecraft.world.item.ItemStack
import kotlin.collections.forEach
import kotlin.collections.plusAssign
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt

class ResearchBookScreen : Screen(Component.translatable("screen.${ModId}.research_book")) {
    private val bookTexture = "textures/gui/book/book.png".ecRL
    private val arrowLeft = "textures/gui/book/arrow_left.png".ecRL
    private val arrowLeftSelected = "textures/gui/book/arrow_left_selected.png".ecRL
    private val arrowRight = "textures/gui/book/arrow_right.png".ecRL
    private val arrowRightSelected = "textures/gui/book/arrow_right_selected.png".ecRL

    private var selectedCategory: Identifier? = null
    private var selectedEntry: BookEntry? = null
    private var spreads = listOf(BookSpread(emptyList()))
    private var spreadIndex = 0
    private var contentRevision = -1L

    private var panX = 0f
    private var panY = 0f
    private var targetPanX = 0f
    private var targetPanY = 0f

    private var zoom = 1f
    private var targetZoom = 1f

    private var categoryScroll = 0f
    private var targetCategoryScroll = 0f

    private val bookmarks = BookBookmarkController()

    private var draggingGraph = false
    private var draggingCategorySlider = false
    private var partialTick = 0f

    private var lastFrameNanos = -1L
    private var frameDt = 0f

    init {
        BookDefaultRenderers.init()
    }

    override fun init() {
        super.init()
        val categories = categories()
        val saved = ClientResearchState.viewState()
        bookmarks.restore(saved, width, height)
        val savedCategory = saved.category?.takeIf { id -> categories.any { it.id == id && ClientResearchState.categoryAvailable(it) } }
        selectedCategory = savedCategory ?: categories.firstOrNull(ClientResearchState::categoryAvailable)?.id
        if (savedCategory != null) {
            targetPanX = saved.panX
            targetPanY = saved.panY
            targetZoom = saved.zoom.coerceIn(0.5f, 2f)
            saved.entry
                ?.let { ResearchCatalog.snapshot().entries[it] }
                ?.takeIf(ClientResearchState::entryAvailable)
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

    override fun tick() {
        super.tick()
        val revision = ClientResearchState.revision()
        val entry = selectedEntry
        if (entry != null && revision != contentRevision) {
            spreads = BookPageLayout.paginate(entry)
            spreadIndex = spreadIndex.coerceIn(0, spreads.lastIndex)
            contentRevision = revision
        }
    }

    override fun extractRenderState(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, partialTick: Float) {
        this.partialTick = partialTick
        this.frameDt = frameDelta()

        panX = approach(panX, targetPanX, frameDt, PAN_HALF_LIFE)
        panY = approach(panY, targetPanY, frameDt, PAN_HALF_LIFE)
        zoom = approach(zoom, targetZoom, frameDt, ZOOM_HALF_LIFE)
        categoryScroll = approach(categoryScroll, targetCategoryScroll, frameDt, SCROLL_HALF_LIFE)
        constrainPan()

        bookmarks.update(frameDt, width, mouseX, mouseY)

        if (selectedEntry == null) {
            renderSpace(graphics)
            renderGraph(graphics, mouseX, mouseY)
        } else {
            graphics.fill(0, 0, width, height, 0xFF080A10.toInt())
            renderBook(graphics, mouseX, mouseY, partialTick)
        }
        bookmarks.renderPicker(graphics)
    }

    override fun mouseClicked(event: MouseButtonEvent, doubleClick: Boolean): Boolean {
        if (event.button() != 0) return super.mouseClicked(event, doubleClick)
        val mouseX = event.x().toInt()
        val mouseY = event.y().toInt()

        if (bookmarks.click(mouseX, mouseY)) return true
        if (selectedEntry != null) return handleBookClick(mouseX, mouseY)
        bookmarks.selectGlobal(mouseX, mouseY, width)?.let { bookmark ->
            ResearchCatalog.snapshot().entries[bookmark.research]?.let { openEntry(it, bookmark.spread) }
            return true
        }
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

        if (bookmarks.drag(event.x().toInt(), event.y().toInt(), dragX, dragY, width, height)) return true

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
        bookmarks.stopDragging()
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
            if (bookmarks.isPickerOpen) {
                bookmarks.close()
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
        return if (abs(result - target) < 0.01f) target else result
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
        bookmarks.renderGlobal(graphics, width, mouseX, mouseY)
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
                graphics.setTooltipForNextFrame(category.title.component(category.titleShadow), mouseX, mouseY.coerceAtLeast(CATEGORY_HEIGHT + 4))
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
        BookThreadRenderer.render(graphics, nodeCenter(from), nodeCenter(to), ClientResearchState.has(to.entry.id))
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
            activeTaskProgress(node.entry).forEach { (definition, progress) ->
                tooltip += Component.literal("${definition.id}: ${progress.current}/${progress.required}")
            }
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

        val bookmarkHovered = localMouseX in 496..<550 && localMouseY in BOOKMARK_Y until BOOKMARK_Y + 16
        bookmarks.renderPage(graphics, entry, spreadIndex, bookmarkHovered, frameDt)
        graphics.blit(RenderPipelines.GUI_TEXTURED, bookTexture, 0, 0, 0f, 0f, BOOK_WIDTH, BOOK_HEIGHT, BOOK_WIDTH, BOOK_HEIGHT)

        renderPageArrows(graphics, localMouseX, localMouseY)

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
        renderTaskLevels(graphics, entry)
    }

    private fun bookTransform(): BookTransform {
        val finalScale = min((width - 40f) / BOOK_WIDTH, (height - 40f) / BOOK_HEIGHT).coerceAtMost(1f)
        return BookTransform(
            ((width - BOOK_WIDTH * finalScale) / 2f).toInt(),
            ((height - BOOK_HEIGHT * finalScale) / 2f).toInt(),
            finalScale
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

        if (shouldShowCompleteButton(entry) && x in COMPLETE_BUTTON_X until COMPLETE_BUTTON_X + COMPLETE_BUTTON_WIDTH &&
            y in COMPLETE_BUTTON_Y until COMPLETE_BUTTON_Y + COMPLETE_BUTTON_HEIGHT
        ) {
            if (tasksComplete(entry)) ResearchNetwork.completeResearch(entry.id)
            return true
        }

        if (x in 496..<550 && y in BOOKMARK_Y until BOOKMARK_Y + 16) {
            bookmarks.open(entry, spreadIndex)
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
        val labelKey = if (hasFinalTaskLevel(entry)) "complete_research" else "complete_task"
        val label = Component.translatable("screen.${ModId}.$labelKey")
        val textX = COMPLETE_BUTTON_X + (COMPLETE_BUTTON_WIDTH - font.width(label)) / 2
        val textY = COMPLETE_BUTTON_Y + (COMPLETE_BUTTON_HEIGHT - font.lineHeight) / 2
        graphics.text(font, label, textX, textY, textColor, false)
    }

    private fun shouldShowCompleteButton(entry: BookEntry): Boolean =
        !entry.automatic && !ClientResearchState.has(entry.id) && isAvailable(entry)

    private fun tasksComplete(entry: BookEntry): Boolean {
        if (entry.taskLevels.isEmpty()) return true
        val active = activeTaskProgress(entry)
        return active.isNotEmpty() && active.all { it.second.complete }
    }

    private fun hasFinalTaskLevel(entry: BookEntry): Boolean =
        entry.taskLevels.isEmpty() || ClientResearchState.completedTaskLevels(entry.id) >= entry.taskLevels.lastIndex

    private fun activeTaskProgress(entry: BookEntry): List<Pair<ResearchTaskDefinition, ResearchTaskProgress>> {
        val levelIndex = ClientResearchState.completedTaskLevels(entry.id)
        val level = entry.taskLevels.getOrNull(levelIndex) ?: return emptyList()
        val offset = entry.taskLevels.take(levelIndex).sumOf { it.tasks.size }
        val progress = ClientResearchState.taskProgress(entry.id)
        return level.tasks.mapIndexedNotNull { index, definition ->
            progress.getOrNull(offset + index)?.let { definition to it }
        }
    }

    private fun renderTaskLevels(graphics: GuiGraphicsExtractor, entry: BookEntry) {
        if (entry.taskLevels.size <= 1) return
        val completed = if (ClientResearchState.has(entry.id)) entry.taskLevels.size else
            ClientResearchState.completedTaskLevels(entry.id)
        entry.taskLevels.indices.forEach { index ->
            val x = width - 8 - (entry.taskLevels.size - index) * 7
            val color = when {
                index < completed -> 0xFF344252.toInt()
                index == completed -> 0xFFFFFFFF.toInt()
                else -> 0xFF87909B.toInt()
            }
            graphics.fill(x, 8, x + 5, 13, color)
            graphics.outline(x, 8, 5, 5, 0xFF202832.toInt())
        }
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
        return ClientResearchState.entryAvailable(entry)
    }

    private fun openEntry(entry: BookEntry, page: Int = 0) {
        ResearchCatalog.snapshot().layout[entry.id]?.let { selectedCategory = it.category }
        selectedEntry = entry
        spreads = BookPageLayout.paginate(entry)
        spreadIndex = page.coerceIn(0, spreads.lastIndex)
        contentRevision = ClientResearchState.revision()
        bookmarks.close()
    }

    private fun closeEntry() {
        selectedEntry = null
        spreads = listOf(BookSpread(emptyList()))
        spreadIndex = 0
        bookmarks.close()
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
        val state = bookmarks.appendTo(
            BookViewState(
                selectedCategory,
                selectedEntry?.id,
                spreadIndex,
                targetPanX,
                targetPanY,
                targetZoom
            )
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

        private const val BOOKMARK_Y = BookBookmarkController.BOOKMARK_Y

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
        private const val SAFE_MARGIN = 28f
        private const val PARALLAX_PAN_RANGE = 8192f
        private const val OFFSET_LOW_BITS = 4
        private const val OFFSET_LOW_MASK = (1 shl OFFSET_LOW_BITS) - 1
        private const val OFFSET_MAX = (1 shl 12) - 1
        private const val ZOOM_MAX = (1 shl 7) - 1

    }
}