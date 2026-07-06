package com.algorithmlx.ecr.api.research

import kotlinx.serialization.json.JsonObject
import net.minecraft.resources.Identifier

interface BookElement {
    val type: Identifier
}

interface BookElementSerializer<T : BookElement> {
    val type: Identifier
    val defaultWidth: Int
    val defaultHeight: Int
    fun decode(json: JsonObject): T
    fun encode(value: T): JsonObject
}

data object SpaceBookElement : BookElement {
    override val type: Identifier = ResearchIds.SPACE
}

data class TaskListBookElement(
    val research: Identifier,
    val level: Int
) : BookElement {
    override val type: Identifier = ResearchIds.TASK_LIST
}

data class TextBookElement(
    val text: BookText,
    val color: Int = 0xFF202020.toInt(),
    val centered: Boolean = false,
    val shadow: Boolean = false,
    val requirement: BookTextRequirement? = null,
    val variants: List<BookTextVariant> = emptyList()
) : BookElement {
    override val type: Identifier = ResearchIds.TEXT
}

data class BookTextVariant(
    val text: BookText,
    val requirement: BookTextRequirement? = null
)

typealias BookTextRequirement = ResearchRequirement

data class ItemBookElement(
    val item: Identifier,
    var count: Int = 1
) : BookElement {
    override val type: Identifier = ResearchIds.ITEM

    init {
        count = count.coerceAtLeast(1)
    }
}

data class BlockBookElement(val block: Identifier) : BookElement {
    override val type: Identifier = ResearchIds.BLOCK
}

data class MultiblockBookElement(
    val multiblock: Identifier,
    val scale: Float = 0.9f,
    val rotationX: Float = 25f,
    val rotationY: Float = -30f,
    val layer: Int = Int.MAX_VALUE
) : BookElement {
    override val type: Identifier = ResearchIds.MULTIBLOCK
}

data class CraftingBookElement(
    val pattern: List<String>,
    val key: Map<Char, ItemBookElement>,
    val result: ItemBookElement
) : BookElement {
    override val type: Identifier = ResearchIds.CRAFTING

    init {
        require(pattern.size in 1..3 && pattern.all { it.length in 1..3 })
    }
}
