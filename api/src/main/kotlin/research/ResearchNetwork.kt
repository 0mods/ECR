package com.algorithmlx.ecr.api.research

import com.algorithmlx.ecr.api.ecRL
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.Identifier
import net.minecraft.server.level.ServerPlayer

data class ResearchSyncPayload(
    val catalog: String,
    val unlocked: Set<Identifier>,
    val favorites: Map<Identifier, Int>,
    val taskProgress: Map<Identifier, List<ResearchTaskProgress>>,
    val bookLevel: Identifier?,
    val viewState: BookViewState
) : CustomPacketPayload {
    override fun type() = TYPE

    companion object {
        @JvmField val TYPE = CustomPacketPayload.Type<ResearchSyncPayload>("research_sync".ecRL)
        @JvmField val STREAM_CODEC: StreamCodec<FriendlyByteBuf, ResearchSyncPayload> = StreamCodec.of(
            { buffer, value ->
                buffer.writeUtf(value.catalog, MAX_CATALOG_SIZE)
                buffer.writeVarInt(value.unlocked.size)
                value.unlocked.forEach(buffer::writeIdentifier)
                buffer.writeVarInt(value.favorites.size)
                value.favorites.forEach { (id, color) ->
                    buffer.writeIdentifier(id)
                    buffer.writeInt(color)
                }
                buffer.writeVarInt(value.taskProgress.size)
                value.taskProgress.forEach { (id, progress) ->
                    buffer.writeIdentifier(id)
                    buffer.writeVarInt(progress.size)
                    progress.forEach {
                        buffer.writeVarInt(it.current)
                        buffer.writeVarInt(it.required)
                    }
                }
                buffer.writeBoolean(value.bookLevel != null)
                value.bookLevel?.let(buffer::writeIdentifier)
                buffer.writeViewState(value.viewState)
            },
            { buffer ->
                val catalog = buffer.readUtf(MAX_CATALOG_SIZE)
                val unlocked = LinkedHashSet<Identifier>().apply {
                    repeat(buffer.readVarInt()) { add(buffer.readIdentifier()) }
                }
                val favorites = LinkedHashMap<Identifier, Int>().apply {
                    repeat(buffer.readVarInt()) { put(buffer.readIdentifier(), buffer.readInt()) }
                }
                val progress = LinkedHashMap<Identifier, List<ResearchTaskProgress>>().apply {
                    repeat(buffer.readVarInt()) {
                        val id = buffer.readIdentifier()
                        put(id, List(buffer.readVarInt()) { ResearchTaskProgress(buffer.readVarInt(), buffer.readVarInt()) })
                    }
                }
                val bookLevel = if (buffer.readBoolean()) buffer.readIdentifier() else null
                ResearchSyncPayload(catalog, unlocked, favorites, progress, bookLevel, buffer.readViewState())
            }
        )
        private const val MAX_CATALOG_SIZE = 8 * 1024 * 1024
    }
}

data class ResearchProgressPayload(
    val taskProgress: Map<Identifier, List<ResearchTaskProgress>>
) : CustomPacketPayload {
    override fun type() = TYPE

    companion object {
        @JvmField val TYPE = CustomPacketPayload.Type<ResearchProgressPayload>("research_progress".ecRL)
        @JvmField val STREAM_CODEC: StreamCodec<FriendlyByteBuf, ResearchProgressPayload> = StreamCodec.of(
            { buffer, value -> buffer.writeTaskProgress(value.taskProgress) },
            { buffer -> ResearchProgressPayload(buffer.readTaskProgress()) }
        )
    }
}

data class UpdateBookViewPayload(val state: BookViewState) : CustomPacketPayload {
    override fun type() = TYPE

    companion object {
        @JvmField val TYPE = CustomPacketPayload.Type<UpdateBookViewPayload>("update_book_view".ecRL)
        @JvmField val STREAM_CODEC: StreamCodec<FriendlyByteBuf, UpdateBookViewPayload> = StreamCodec.of(
            { buffer, value -> buffer.writeViewState(value.state) },
            { buffer -> UpdateBookViewPayload(buffer.readViewState()) }
        )
    }
}

data class CompleteResearchPayload(val research: Identifier) : CustomPacketPayload {
    override fun type() = TYPE

    companion object {
        @JvmField val TYPE = CustomPacketPayload.Type<CompleteResearchPayload>("complete_research".ecRL)
        @JvmField val STREAM_CODEC: StreamCodec<FriendlyByteBuf, CompleteResearchPayload> = StreamCodec.of(
            { buffer, value -> buffer.writeIdentifier(value.research) },
            { buffer -> CompleteResearchPayload(buffer.readIdentifier()) }
        )
    }
}

data class FavoriteResearchPayload(val research: Identifier, val color: Int?) : CustomPacketPayload {
    override fun type() = TYPE

    companion object {
        @JvmField val TYPE = CustomPacketPayload.Type<FavoriteResearchPayload>("favorite_research".ecRL)
        @JvmField val STREAM_CODEC: StreamCodec<FriendlyByteBuf, FavoriteResearchPayload> = StreamCodec.of(
            { buffer, value ->
                buffer.writeIdentifier(value.research)
                buffer.writeBoolean(value.color != null)
                value.color?.let(buffer::writeInt)
            },
            { buffer -> FavoriteResearchPayload(buffer.readIdentifier(), if (buffer.readBoolean()) buffer.readInt() else null) }
        )
    }
}

object ResearchNetwork {
    @JvmField var sendToPlayer: (ServerPlayer, ResearchSyncPayload) -> Unit = { _, _ -> }
    @JvmField var sendProgressToPlayer: (ServerPlayer, ResearchProgressPayload) -> Unit = { _, _ -> }
    @JvmField var completeResearch: (Identifier) -> Unit = {}
    @JvmField var updateFavorite: (Identifier, Int?) -> Unit = { _, _ -> }
    @JvmField var updateView: (BookViewState) -> Unit = {}
}

object ClientResearchState {
    @Volatile private var unlockedResearch = emptySet<Identifier>()
    @Volatile private var favoriteResearch = emptyMap<Identifier, Int>()
    @Volatile private var progress = emptyMap<Identifier, List<ResearchTaskProgress>>()
    @Volatile private var currentBookLevel: Identifier? = null
    @Volatile private var savedViewState = BookViewState()

    @JvmStatic fun apply(payload: ResearchSyncPayload) {
        ResearchCatalog.importJson(payload.catalog)
        unlockedResearch = payload.unlocked.toSet()
        favoriteResearch = payload.favorites.toMap()
        progress = payload.taskProgress.toMap()
        currentBookLevel = payload.bookLevel
        savedViewState = payload.viewState
    }

    @JvmStatic fun apply(payload: ResearchProgressPayload) {
        progress = payload.taskProgress.toMap()
    }

    @JvmStatic fun has(research: Identifier): Boolean = research in unlockedResearch
    @JvmStatic fun unlocked(): Set<Identifier> = unlockedResearch
    @JvmStatic fun favorites(): Map<Identifier, Int> = favoriteResearch
    @JvmStatic fun favoriteColor(research: Identifier): Int? = favoriteResearch[research]
    @JvmStatic fun taskProgress(research: Identifier): List<ResearchTaskProgress> = progress[research].orEmpty()
    @JvmStatic fun bookLevel(): Identifier? = currentBookLevel
    @JvmStatic fun viewState(): BookViewState = savedViewState
    @JvmStatic fun updateLocalView(state: BookViewState) {
        savedViewState = state
    }
    @JvmStatic fun categoryAvailable(category: BookCategory): Boolean =
        category.dependencies.all(::has) && ResearchProgress.meetsBookLevel(currentBookLevel, category.bookLevel)
}

private fun FriendlyByteBuf.writeTaskProgress(progress: Map<Identifier, List<ResearchTaskProgress>>) {
    writeVarInt(progress.size)
    progress.forEach { (id, tasks) ->
        writeIdentifier(id)
        writeVarInt(tasks.size)
        tasks.forEach {
            writeVarInt(it.current)
            writeVarInt(it.required)
        }
    }
}

private fun FriendlyByteBuf.readTaskProgress(): Map<Identifier, List<ResearchTaskProgress>> =
    LinkedHashMap<Identifier, List<ResearchTaskProgress>>().apply {
        repeat(readVarInt()) {
            put(readIdentifier(), List(readVarInt()) { ResearchTaskProgress(readVarInt(), readVarInt()) })
        }
    }

private fun FriendlyByteBuf.writeViewState(state: BookViewState) {
    writeBoolean(state.category != null)
    state.category?.let(::writeIdentifier)
    writeBoolean(state.entry != null)
    state.entry?.let(::writeIdentifier)
    writeVarInt(state.spread.coerceAtLeast(0))
    writeFloat(state.panX)
    writeFloat(state.panY)
    writeFloat(state.zoom)
}

private fun FriendlyByteBuf.readViewState() = BookViewState(
    if (readBoolean()) readIdentifier() else null,
    if (readBoolean()) readIdentifier() else null,
    readVarInt(),
    readFloat(),
    readFloat(),
    readFloat()
)
