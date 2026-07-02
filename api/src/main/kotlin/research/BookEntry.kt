package com.algorithmlx.ecr.api.research

import kotlinx.serialization.json.JsonObject

interface BookEntry {
    val entryDatas: List<BookEntryData>
}

interface BookEntryData {
    val content: BookContent

    val dependency: Dependency

    data class Dependency(val dependency: BookEntryData, val align: DependencyAlign) {
        enum class DependencyAlign {
            AFTER, BEFORE
        }
    }
}

interface BookContent {
    val serializer: BookContentSerializer<*>
}

interface BookContentSerializer<T: BookContent> {
    fun toJson(content: T): JsonObject

    fun fromJson(jsonObject: JsonObject): T
}
