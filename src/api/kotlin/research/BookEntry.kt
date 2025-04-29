package team._0mods.ecr.api.research

import de.fabmax.kool.scene.Scene
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject

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
    val render: Scene.() -> Unit get() = {}

    val serializer: BookContentSerializer<*>
}

interface BookContentSerializer<T: BookContent> {
    fun toJson(content: T): JsonObject

    fun fromJson(jsonObject: JsonObject): T
}