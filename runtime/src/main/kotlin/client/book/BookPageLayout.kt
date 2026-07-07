package com.algorithmlx.ecr.client.book

import com.algorithmlx.ecr.api.research.content.BookElementAlign
import com.algorithmlx.ecr.api.research.content.BookElementSpec
import com.algorithmlx.ecr.api.research.content.BookEntry
import com.algorithmlx.ecr.api.research.content.BookText
import com.algorithmlx.ecr.api.research.content.BookTextVariant
import com.algorithmlx.ecr.api.research.ClientResearchState
import com.algorithmlx.ecr.api.research.content.CraftingBookElement
import com.algorithmlx.ecr.api.research.serializer.ResearchSerializers
import com.algorithmlx.ecr.api.research.content.SpaceBookElement
import com.algorithmlx.ecr.api.research.content.TaskListBookElement
import com.algorithmlx.ecr.api.research.content.TextBookElement
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.util.FormattedCharSequence
import kotlin.math.ceil

data class BookElementPlacement(
    val element: BookElementSpec,
    val x: Int,
    val y: Int,
    val width: Int,
    val height: Int,
    val textLines: List<FormattedCharSequence>? = null
)

data class BookSpread(val elements: List<BookElementPlacement>)

object BookPageLayout {
    private const val FIRST_X = 16
    private const val SECOND_X = 271
    private const val TOP = 16
    private const val BOTTOM = 240
    private const val PAGE_WIDTH = 225
    private const val PAGE_HEIGHT = BOTTOM - TOP

    fun paginate(entry: BookEntry): List<BookSpread> {
        val spreads = mutableListOf<MutableList<BookElementPlacement>>(mutableListOf())
        val cursor = PageCursor(spreads)

        taskElement(entry)?.let { spec ->
            val height = spec.height ?: TASK_CELL_SIZE
            spreads.last() += BookElementPlacement(spec, cursor.alignedX(PAGE_WIDTH), cursor.y, PAGE_WIDTH, height)
            cursor.y += height
        }

        entry.pages.flatMap { it.elements }.forEach { originalSpec ->
            val spec = originalSpec.resolveText(entry) ?: return@forEach
            val serializer = ResearchSerializers.elementSerializer(spec.content.type)
            val width = (spec.width ?: autoWidth(spec) ?: serializer?.defaultWidth ?: 16).coerceIn(0, PAGE_WIDTH)
            val height = (spec.height ?: autoHeight(spec, width) ?: serializer?.defaultHeight ?: 16).coerceIn(0, PAGE_HEIGHT)
            if (spec.content === SpaceBookElement) {
                cursor.y = (cursor.y + height).coerceAtMost(BOTTOM)
                return@forEach
            }
            if (spec.content is TextBookElement) {
                placeText(spec, width.coerceAtLeast(1), cursor)
                return@forEach
            }
            if (cursor.y + height > BOTTOM) {
                cursor.nextSide()
            }
            spreads.last() += BookElementPlacement(spec, cursor.alignedX(width, spec.align), cursor.y, width, height)
            cursor.y += height
        }
        return spreads.map(::BookSpread).ifEmpty { listOf(BookSpread(emptyList())) }
    }

    private fun taskElement(entry: BookEntry): BookElementSpec? {
        if (entry.taskLevels.isEmpty()) return null
        val level = if (ClientResearchState.has(entry.id)) entry.taskLevels.lastIndex else
            ClientResearchState.completedTaskLevels(entry.id).coerceIn(0, entry.taskLevels.lastIndex)
        val visibleTasks = entry.taskLevels[level].tasks.count { !it.hidden }
        if (visibleTasks == 0) return null
        val rows = ceil(visibleTasks / TASKS_PER_ROW.toFloat()).toInt().coerceAtLeast(1)
        return BookElementSpec(TaskListBookElement(entry.id, level), PAGE_WIDTH, rows * TASK_CELL_SIZE + 4)
    }

    private fun autoWidth(spec: BookElementSpec): Int? {
        val text = spec.content as? TextBookElement ?: return null
        return Minecraft.getInstance().font.width(text.text.component()).coerceIn(1, PAGE_WIDTH)
    }

    private fun autoHeight(spec: BookElementSpec, width: Int): Int? =
        (spec.content as? CraftingBookElement)
            ?.let { BookRecipeElementRenderer.preferredHeight(it, width) }

    private fun placeText(
        spec: BookElementSpec,
        width: Int,
        cursor: PageCursor
    ) {
        val font = Minecraft.getInstance().font
        val element = spec.content as TextBookElement
        val lines = font.split(element.text.component(), width)
        if (lines.isEmpty()) return
        var line = 0
        while (line < lines.size) {
            val availableLines = (BOTTOM - cursor.y) / font.lineHeight
            if (availableLines == 0) {
                cursor.nextSide()
                continue
            }
            val lineCount = minOf(availableLines, lines.size - line)
            val chunk = lines.subList(line, line + lineCount)
            val height = chunk.size * font.lineHeight
            cursor.spreads.last() += BookElementPlacement(spec, cursor.alignedX(width, spec.align), cursor.y, width, height, chunk)
            line += lineCount
            cursor.y += height
            if (line < lines.size) {
                cursor.nextSide()
            }
        }
    }

    private fun BookText.component(): Component = if (translated) Component.translatable(value) else Component.literal(value)

    private fun BookElementSpec.resolveText(entry: BookEntry): BookElementSpec? {
        val element = content as? TextBookElement ?: return this
        val candidates = buildList {
            add(BookTextVariant(element.text, element.requirement))
            addAll(element.variants)
        }
        val selected = candidates.lastOrNull { variant ->
            variant.requirement?.let { ClientResearchState.requirementMet(entry.id, it) } == true
        }
            ?: candidates.lastOrNull { it.requirement == null }
            ?: return null
        return copy(content = element.copy(text = selected.text, requirement = null, variants = emptyList()))
    }

    private class PageCursor(val spreads: MutableList<MutableList<BookElementPlacement>>) {
        var side = 0
        var y = TOP
        val x get() = if (side % 2 == 0) FIRST_X else SECOND_X

        fun alignedX(width: Int, align: BookElementAlign = BookElementAlign.LEFT): Int = when (align) {
            BookElementAlign.LEFT -> x
            BookElementAlign.CENTER -> x + (PAGE_WIDTH - width) / 2
            BookElementAlign.RIGHT -> x + PAGE_WIDTH - width
        }

        fun nextSide() {
            side++
            y = TOP
            if (side % 2 == 0) spreads.add(mutableListOf())
        }
    }

    private const val TASK_CELL_SIZE = 20
    private const val TASKS_PER_ROW = PAGE_WIDTH / TASK_CELL_SIZE
}
