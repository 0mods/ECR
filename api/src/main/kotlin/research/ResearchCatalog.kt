package com.algorithmlx.ecr.api.research

import com.algorithmlx.ecr.api.registries.ECRegistries
import net.minecraft.resources.Identifier
import java.util.Collections
import java.util.concurrent.CopyOnWriteArrayList

data class ResearchCatalogSnapshot(
    val categories: Map<Identifier, BookCategory>,
    val entries: Map<Identifier, BookEntry>,
    val layout: Map<Identifier, ResolvedBookEntry>
) {
    fun entriesIn(category: Identifier): List<ResolvedBookEntry> =
        layout.values.filter { it.category == category }
}

object ResearchCatalog {
    private val permanentCategories = LinkedHashMap<Identifier, BookCategory>()
    private val permanentEntries = LinkedHashMap<Identifier, BookEntry>()
    private val reloadListeners = CopyOnWriteArrayList<(ResearchCatalogSnapshot) -> Unit>()
    @Volatile private var current = ResearchCatalogSnapshot(emptyMap(), emptyMap(), emptyMap())

    @JvmStatic
    fun snapshot(): ResearchCatalogSnapshot = current

    @JvmStatic
    @Synchronized
    fun register(category: BookCategory) {
        check(permanentCategories.putIfAbsent(category.id, category) == null) { "Duplicate category: ${category.id}" }
        replace(emptyList(), emptyList())
    }

    @JvmStatic
    @Synchronized
    fun register(entry: BookEntry) {
        check(permanentEntries.putIfAbsent(entry.id, entry) == null) { "Duplicate research: ${entry.id}" }
        replace(emptyList(), emptyList())
    }

    @JvmStatic
    fun onReload(listener: (ResearchCatalogSnapshot) -> Unit) {
        reloadListeners += listener
    }

    @JvmStatic
    @Synchronized
    fun replace(categories: Collection<BookCategory>, entries: Collection<BookEntry>) {
        val categoryMap = LinkedHashMap(permanentCategories)
        categories.forEach { check(categoryMap.put(it.id, it) == null) { "Duplicate category: ${it.id}" } }
        val entryMap = LinkedHashMap(permanentEntries)
        entries.forEach { check(entryMap.put(it.id, it) == null) { "Duplicate research: ${it.id}" } }
        install(categoryMap, entryMap)
    }

    private fun install(categoryMap: LinkedHashMap<Identifier, BookCategory>, entryMap: LinkedHashMap<Identifier, BookEntry>) {
        val layout = ResearchLayout.resolve(categoryMap, entryMap)
        current = ResearchCatalogSnapshot(
            Collections.unmodifiableMap(categoryMap),
            Collections.unmodifiableMap(entryMap),
            Collections.unmodifiableMap(layout)
        )
        reloadListeners.forEach { it(current) }
    }

    @JvmStatic
    fun exportJson(): String = current.let { ResearchJson.encodeCatalog(it.categories.values, it.entries.values) }

    @JvmStatic
    fun importJson(json: String) {
        val (categories, entries) = ResearchJson.decodeCatalog(json)
        synchronized(this) {
            install(
                LinkedHashMap<Identifier, BookCategory>().apply { categories.forEach { put(it.id, it) } },
                LinkedHashMap<Identifier, BookEntry>().apply { entries.forEach { put(it.id, it) } }
            )
        }
    }
}

private object ResearchLayout {
    fun resolve(
        categories: Map<Identifier, BookCategory>,
        entries: Map<Identifier, BookEntry>
    ): LinkedHashMap<Identifier, ResolvedBookEntry> {
        if (entries.isNotEmpty()) require(categories.isNotEmpty()) { "Research entries require at least one category" }
        categories.values.forEach { category ->
            category.dependencies.forEach { require(it in entries) { "Unknown dependency $it in category ${category.id}" } }
            category.bookLevel?.let { require(ECRegistries.BOOK_LEVEL.containsKey(it)) { "Unknown book level $it in category ${category.id}" } }
        }
        entries.values.forEach { entry ->
            entry.category?.let { require(it in categories) { "Unknown category $it in ${entry.id}" } }
            entry.dependencies.forEach { require(it in entries) { "Unknown dependency $it in ${entry.id}" } }
            entry.requirements.forEach { validateRequirement(entry.id, it, entries) }
            validateTextRequirements(entry, entries)
        }
        val result = LinkedHashMap<Identifier, ResolvedBookEntry>()
        val visiting = HashSet<Identifier>()
        entries.keys.forEach { resolveEntry(it, categories, entries, result, visiting) }
        return result
    }

    private fun validateTextRequirements(entry: BookEntry, entries: Map<Identifier, BookEntry>) {
        entry.pages.asSequence()
            .flatMap { it.elements.asSequence() }
            .mapNotNull { it.content as? TextBookElement }
            .flatMap { element ->
                sequenceOf(element.requirement) + element.variants.asSequence().map(BookTextVariant::requirement)
            }
            .filterNotNull()
            .forEach { validateRequirement(entry.id, it, entries) }
    }

    private fun validateRequirement(owner: Identifier, requirement: ResearchRequirement, entries: Map<Identifier, BookEntry>) {
        val targetId = requirement.researchId(owner)
        val target = entries[targetId] ?: error("Unknown research $targetId in $owner")
        requirement.taskId?.let { taskId ->
            val ids = target.taskLevels.map(ResearchTaskLevel::id) + target.taskDefinitions.map(ResearchTaskDefinition::id)
            require(taskId in ids) { "Unknown task ID $taskId in $targetId, referenced by $owner" }
        }
    }

    private fun resolveEntry(
        id: Identifier,
        categories: Map<Identifier, BookCategory>,
        entries: Map<Identifier, BookEntry>,
        result: LinkedHashMap<Identifier, ResolvedBookEntry>,
        visiting: MutableSet<Identifier>
    ): ResolvedBookEntry {
        result[id]?.let { return it }
        check(visiting.add(id)) { "Cyclic research dependency at $id" }
        val entry = entries.getValue(id)
        val dependencies = entry.dependencies.map { resolveEntry(it, categories, entries, result, visiting) }
        val category = entry.category ?: dependencies.lastOrNull()?.category
            ?: categories.values.minWith(compareBy<BookCategory> { it.order }.thenBy { it.id.toString() }).id
        val parent = dependencies.lastOrNull()
        val desired = entry.position
            ?: parent?.let { alignedPosition(entry, it) }
            ?: parent
                ?.takeIf { entry.category == null }
                ?.let { BookPosition(it.position.x, it.position.y + nodeHeight(it.entry) + NODE_GAP) }
        val position = findFree(category, desired, entry, result.values)
        val resolved = ResolvedBookEntry(entry, category, position)
        result[id] = resolved
        visiting.remove(id)
        return resolved
    }

    private fun alignedPosition(entry: BookEntry, parent: ResolvedBookEntry): BookPosition? {
        val align = entry.align
        if (align.isEmpty()) return null
        if (BookEntryAlign.UP in align && BookEntryAlign.DOWN in align) return null
        if (BookEntryAlign.LEFT in align && BookEntryAlign.RIGHT in align) return null

        var x = parent.position.x
        var y = parent.position.y
        when {
            BookEntryAlign.LEFT in align -> x -= nodeWidth(entry) + NODE_GAP
            BookEntryAlign.RIGHT in align -> x += nodeWidth(parent.entry) + NODE_GAP
        }
        when {
            BookEntryAlign.UP in align -> y -= nodeHeight(entry) + NODE_GAP
            BookEntryAlign.DOWN in align -> y += nodeHeight(parent.entry) + NODE_GAP
        }
        return BookPosition(x, y)
    }

    private fun findFree(
        category: Identifier,
        desired: BookPosition?,
        entry: BookEntry,
        existing: Collection<ResolvedBookEntry>
    ): BookPosition {
        val occupied = existing.filter { it.category == category }
        if (desired != null && occupied.none { overlaps(desired, entry, it.position, it.entry) }) return desired
        var y = 48
        while (y < 4096) {
            var x = 32
            while (x < 1024) {
                val candidate = BookPosition(x, y)
                if (occupied.none { overlaps(candidate, entry, it.position, it.entry) }) return candidate
                x += 48
            }
            y += 48
        }
        error("Unable to place research ${entry.id}")
    }

    private fun overlaps(a: BookPosition, aEntry: BookEntry, b: BookPosition, bEntry: BookEntry): Boolean {
        val padding = NODE_GAP
        return a.x < b.x + nodeWidth(bEntry) + padding &&
            a.x + nodeWidth(aEntry) + padding > b.x &&
            a.y < b.y + nodeHeight(bEntry) + padding &&
            a.y + nodeHeight(aEntry) + padding > b.y
    }

    private fun nodeWidth(entry: BookEntry) = entry.frame?.width ?: 18
    private fun nodeHeight(entry: BookEntry) = entry.frame?.height ?: 18

    private const val NODE_GAP = 14
}
