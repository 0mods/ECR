package com.algorithmlx.ecr.api.research

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import net.minecraft.resources.Identifier
import java.util.concurrent.ConcurrentHashMap

internal val researchJson = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
    explicitNulls = false
}

object ResearchSerializers {
    private val elementSerializers = ConcurrentHashMap<Identifier, BookElementSerializer<out BookElement>>()
    private val taskSerializers = ConcurrentHashMap<Identifier, ResearchTaskSerializer<out ResearchTask>>()

    init {
        registerElement(SpaceElementSerializer)
        registerElement(TextElementSerializer)
        registerElement(ItemElementSerializer)
        registerElement(BlockElementSerializer)
        registerElement(MultiblockElementSerializer)
        registerElement(CraftingElementSerializer)
        registerTask(ItemTaskSerializer)
        registerTask(ExperienceTaskSerializer)
        registerTask(CraftingTaskSerializer)
    }

    @JvmStatic
    fun <T : BookElement> registerElement(serializer: BookElementSerializer<T>) {
        check(elementSerializers.putIfAbsent(serializer.type, serializer) == null) { "Duplicate book element serializer: ${serializer.type}" }
    }

    @JvmStatic
    fun <T : ResearchTask> registerTask(serializer: ResearchTaskSerializer<T>) {
        check(taskSerializers.putIfAbsent(serializer.type, serializer) == null) { "Duplicate research task serializer: ${serializer.type}" }
    }

    @Suppress("UNCHECKED_CAST")
    fun decodeElement(type: Identifier, json: JsonObject): BookElement =
        (elementSerializers[type] as? BookElementSerializer<BookElement>)?.decode(json)
            ?: error("Unknown book element type: $type")

    @Suppress("UNCHECKED_CAST")
    fun encodeElement(value: BookElement): JsonObject =
        (elementSerializers[value.type] as? BookElementSerializer<BookElement>)?.encode(value)
            ?: error("Unknown book element type: ${value.type}")

    @Suppress("UNCHECKED_CAST")
    fun decodeTask(type: Identifier, json: JsonObject): ResearchTask =
        (taskSerializers[type] as? ResearchTaskSerializer<ResearchTask>)?.decode(json)
            ?: error("Unknown research task type: $type")

    @Suppress("UNCHECKED_CAST")
    fun encodeTask(value: ResearchTask): JsonObject =
        (taskSerializers[value.type] as? ResearchTaskSerializer<ResearchTask>)?.encode(value)
            ?: error("Unknown research task type: ${value.type}")

    fun elementSerializer(type: Identifier): BookElementSerializer<out BookElement>? = elementSerializers[type]
}

@Serializable
private data class TextElementDto(
    val text: JsonElement,
    val color: Int = 0xFF202020.toInt(),
    val centered: Boolean = false,
    val shadow: Boolean = false,
    val requirement: TextRequirementDto? = null,
    val variants: List<TextVariantDto> = emptyList()
)

@Serializable
private data class TextVariantDto(
    val text: JsonElement,
    val requirement: TextRequirementDto? = null
)

@Serializable
private data class TextRequirementDto(
    val taskid: String? = null,
    val research: String? = null,
    val requirement: String? = null
)

@Serializable
private data class ItemElementDto(val item: String, val count: Int = 1)

@Serializable
private data class BlockElementDto(val block: String)

@Serializable
private data class MultiblockElementDto(
    val multiblock: String,
    val scale: Float = 0.9f,
    @SerialName("rotation_x") val rotationX: Float = 25f,
    @SerialName("rotation_y") val rotationY: Float = -30f,
    val layer: Int = Int.MAX_VALUE
)

@Serializable
private data class CraftingElementDto(
    val pattern: List<String>,
    val key: Map<String, ItemElementDto>,
    val result: ItemElementDto
)

@Serializable
private data class ItemTaskDto(val item: String, val count: Int = 1, val consume: Boolean = false)

@Serializable
private data class ExperienceTaskDto(val amount: Int = 1, val levels: Boolean = false, val consume: Boolean = false)

@Serializable
private data class CraftingTaskDto(val recipe: String)

private object SpaceElementSerializer : BookElementSerializer<SpaceBookElement> {
    override val type = ResearchIds.SPACE
    override val defaultWidth = 0
    override val defaultHeight = 8
    override fun decode(json: JsonObject) = SpaceBookElement
    override fun encode(value: SpaceBookElement) = JsonObject(emptyMap())
}

private object TextElementSerializer : BookElementSerializer<TextBookElement> {
    override val type = ResearchIds.TEXT
    override val defaultWidth = 208
    override val defaultHeight = 18
    override fun decode(json: JsonObject): TextBookElement = researchJson.decodeFromJsonElement<TextElementDto>(json).let {
        TextBookElement(
            it.text.toBookText(),
            it.color,
            it.centered,
            it.shadow,
            it.requirement?.toModel(),
            it.variants.map { variant -> BookTextVariant(variant.text.toBookText(), variant.requirement?.toModel()) }
        )
    }
    override fun encode(value: TextBookElement): JsonObject = researchJson.encodeToJsonElement(
        TextElementDto(
            value.text.toJsonElement(),
            value.color,
            value.centered,
            value.shadow,
            value.requirement?.toDto(),
            value.variants.map { TextVariantDto(it.text.toJsonElement(), it.requirement?.toDto()) }
        )
    ).jsonObject
}

private object ItemElementSerializer : BookElementSerializer<ItemBookElement> {
    override val type = ResearchIds.ITEM
    override val defaultWidth = 18
    override val defaultHeight = 18
    override fun decode(json: JsonObject): ItemBookElement = researchJson.decodeFromJsonElement<ItemElementDto>(json).toElement()
    override fun encode(value: ItemBookElement): JsonObject = researchJson.encodeToJsonElement(value.toDto()).jsonObject
}

private object BlockElementSerializer : BookElementSerializer<BlockBookElement> {
    override val type = ResearchIds.BLOCK
    override val defaultWidth = 18
    override val defaultHeight = 18
    override fun decode(json: JsonObject): BlockBookElement = researchJson.decodeFromJsonElement<BlockElementDto>(json).let {
        BlockBookElement(Identifier.parse(it.block))
    }
    override fun encode(value: BlockBookElement): JsonObject = researchJson.encodeToJsonElement(BlockElementDto(value.block.toString())).jsonObject
}

private object MultiblockElementSerializer : BookElementSerializer<MultiblockBookElement> {
    override val type = ResearchIds.MULTIBLOCK
    override val defaultWidth = 208
    override val defaultHeight = 96
    override fun decode(json: JsonObject): MultiblockBookElement = researchJson.decodeFromJsonElement<MultiblockElementDto>(json).let {
        MultiblockBookElement(Identifier.parse(it.multiblock), it.scale, it.rotationX, it.rotationY, it.layer)
    }
    override fun encode(value: MultiblockBookElement): JsonObject = researchJson.encodeToJsonElement(
        MultiblockElementDto(value.multiblock.toString(), value.scale, value.rotationX, value.rotationY, value.layer)
    ).jsonObject
}

private object CraftingElementSerializer : BookElementSerializer<CraftingBookElement> {
    override val type = ResearchIds.CRAFTING
    override val defaultWidth = 122
    override val defaultHeight = 58
    override fun decode(json: JsonObject): CraftingBookElement = researchJson.decodeFromJsonElement<CraftingElementDto>(json).let { dto ->
        CraftingBookElement(
            dto.pattern,
            dto.key.mapKeys { (symbol) -> symbol.single() }.mapValues { it.value.toElement() },
            dto.result.toElement()
        )
    }
    override fun encode(value: CraftingBookElement): JsonObject = researchJson.encodeToJsonElement(
        CraftingElementDto(value.pattern, value.key.mapKeys { it.key.toString() }.mapValues { it.value.toDto() }, value.result.toDto())
    ).jsonObject
}

private object ItemTaskSerializer : ResearchTaskSerializer<ItemResearchTask> {
    override val type = ResearchIds.ITEM_TASK
    override fun decode(json: JsonObject): ItemResearchTask = researchJson.decodeFromJsonElement<ItemTaskDto>(json).let {
        ItemResearchTask(it.item, it.count.coerceAtLeast(1), it.consume)
    }
    override fun encode(value: ItemResearchTask): JsonObject = researchJson.encodeToJsonElement(
        ItemTaskDto(value.item, value.count, value.consumeItems)
    ).jsonObject
}

private object CraftingTaskSerializer : ResearchTaskSerializer<CraftingResearchTask> {
    override val type = ResearchIds.CRAFTING_TASK
    override fun decode(json: JsonObject): CraftingResearchTask = researchJson.decodeFromJsonElement<CraftingTaskDto>(json).let {
        CraftingResearchTask(Identifier.parse(it.recipe))
    }
    override fun encode(value: CraftingResearchTask): JsonObject = researchJson.encodeToJsonElement(
        CraftingTaskDto(value.recipe.toString())
    ).jsonObject
}

private object ExperienceTaskSerializer : ResearchTaskSerializer<ExperienceResearchTask> {
    override val type = ResearchIds.EXPERIENCE_TASK
    override fun decode(json: JsonObject): ExperienceResearchTask = researchJson.decodeFromJsonElement<ExperienceTaskDto>(json).let {
        ExperienceResearchTask(it.amount.coerceAtLeast(1), it.levels, it.consume)
    }
    override fun encode(value: ExperienceResearchTask): JsonObject = researchJson.encodeToJsonElement(
        ExperienceTaskDto(value.amount, value.levels, value.consumeExperience)
    ).jsonObject
}

internal fun JsonElement.toBookText(): BookText {
    if (this is JsonPrimitive && isString) return BookText(content, false)
    val objectValue = jsonObject
    objectValue["translate"]?.jsonPrimitive?.content?.let { return BookText(it, true) }
    objectValue["text"]?.jsonPrimitive?.content?.let { return BookText(it, false) }
    error("Book text must contain 'translate' or 'text'")
}

internal fun BookText.toJsonElement(): JsonElement = buildJsonObject {
    put(if (translated) "translate" else "text", value)
}

private fun ItemElementDto.toElement() = ItemBookElement(Identifier.parse(item), count)
private fun ItemBookElement.toDto() = ItemElementDto(item.toString(), count)
private fun TextRequirementDto.toModel(): BookTextRequirement {
    requirement?.let { return ResearchJson.parseRequirement(it, null) }
    if (research == null && taskid?.contains(':') == true) return ResearchJson.parseRequirement(taskid, null)
    return BookTextRequirement(research?.let(Identifier::parse), taskid)
}
private fun BookTextRequirement.toDto() = TextRequirementDto(taskid = taskId, research = research?.toString())
