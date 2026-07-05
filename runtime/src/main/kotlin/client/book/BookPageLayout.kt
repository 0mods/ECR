package com.algorithmlx.ecr.client.book

import com.algorithmlx.ecr.api.research.BookElementSpec
import com.algorithmlx.ecr.api.research.BookEntry
import com.algorithmlx.ecr.api.research.BookText
import com.algorithmlx.ecr.api.research.ResearchSerializers
import com.algorithmlx.ecr.api.research.SpaceBookElement
import com.algorithmlx.ecr.api.research.TextBookElement
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.util.FormattedCharSequence

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

        entry.pages.flatMap { it.elements }.forEach { spec ->
            val serializer = ResearchSerializers.elementSerializer(spec.content.type)
            val width = (spec.width ?: serializer?.defaultWidth ?: 16).coerceIn(0, PAGE_WIDTH)
            val height = (spec.height ?: serializer?.defaultHeight ?: 16).coerceIn(0, PAGE_HEIGHT)
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
            spreads.last() += BookElementPlacement(spec, cursor.x, cursor.y, width, height)
            cursor.y += height
        }
        return spreads.map(::BookSpread).ifEmpty { listOf(BookSpread(emptyList())) }
    }

    private fun placeText(
        spec: BookElementSpec,
        width: Int,
        cursor: PageCursor
    ) {
        val font = Minecraft.getInstance().font
        val element = spec.content as TextBookElement
        val lines = font.split(element.text.component(), width)
        if (lines.isEmpty()) return

        val totalHeight = lines.size * font.lineHeight
        if (totalHeight <= PAGE_HEIGHT) {
            if (cursor.y + totalHeight > BOTTOM) {
                cursor.nextSide()
            }
            cursor.spreads.last() += BookElementPlacement(spec, cursor.x, cursor.y, width, totalHeight, lines)
            cursor.y += totalHeight
            return
        }

        if (cursor.y > TOP) {
            cursor.nextSide()
        }
        var line = 0
        while (line < lines.size) {
            val lineCount = minOf((BOTTOM - cursor.y) / font.lineHeight, lines.size - line)
            val chunk = lines.subList(line, line + lineCount)
            val height = chunk.size * font.lineHeight
            cursor.spreads.last() += BookElementPlacement(spec, cursor.x, cursor.y, width, height, chunk)
            line += lineCount
            cursor.y += height
            if (line < lines.size) {
                cursor.nextSide()
            }
        }
    }

    private fun BookText.component(): Component = if (translated) Component.translatable(value) else Component.literal(value)

    private class PageCursor(val spreads: MutableList<MutableList<BookElementPlacement>>) {
        var side = 0
        var y = TOP
        val x get() = if (side % 2 == 0) FIRST_X else SECOND_X

        fun nextSide() {
            side++
            y = TOP
            if (side % 2 == 0) spreads.add(mutableListOf())
        }
    }
}
