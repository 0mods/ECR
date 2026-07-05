package com.algorithmlx.ecr.api.research

import net.minecraft.resources.Identifier

data class BookEntry(
    val id: Identifier,
    val title: BookText,
    val category: Identifier? = null,
    val icon: BookIcon = BookIcon(),
    val frame: BookFrame? = BookFrame(),
    val position: BookPosition? = null,
    val dependencies: List<Identifier> = emptyList(),
    val pages: List<BookPage> = emptyList(),
    val tasks: List<ResearchTask> = emptyList(),
    val locks: List<ResearchLock> = emptyList(),
    val automatic: Boolean = tasks.isEmpty(),
    val hiddenUntilAvailable: Boolean = false,
    val titleShadow: Boolean = false,
    val align: Set<BookEntryAlign> = emptySet()
)

enum class BookEntryAlign {
    UP,
    DOWN,
    LEFT,
    RIGHT
}

data class BookFrame(
    val texture: Identifier = ResearchIds.DEFAULT_FRAME,
    val width: Int = 32,
    val height: Int = 32,
    val itemX: Int = 8,
    val itemY: Int = 8,
    val itemSize: Int = 16
) {
    init {
        require(width > 0 && height > 0 && itemSize >= 0)
    }
}

data class BookPosition(val x: Int, val y: Int)

data class BookPage(val elements: List<BookElementSpec>)

data class BookElementSpec(
    val content: BookElement,
    val width: Int? = null,
    val height: Int? = null
)

data class ResolvedBookEntry(
    val entry: BookEntry,
    val category: Identifier,
    val position: BookPosition
)

enum class ResearchTargetType {
    ITEM,
    BLOCK,
    ENTITY
}

enum class ResearchAction {
    USE,
    ATTACK,
    BREAK,
    PLACE,
    INTERACT
}

data class ResearchLock(
    val targetType: ResearchTargetType,
    val target: Identifier,
    val actions: Set<ResearchAction> = setOf(ResearchAction.USE, ResearchAction.INTERACT)
)
