package com.algorithmlx.ecr.api.research

import com.algorithmlx.ecr.api.ecRL
import com.algorithmlx.ecr.api.registries.ECRegistries
import com.mojang.serialization.Codec
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import net.minecraft.resources.Identifier
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer
import net.minecraft.util.datafix.DataFixTypes
import net.minecraft.world.level.saveddata.SavedData
import net.minecraft.world.level.saveddata.SavedDataType
import java.util.UUID

data class PlayerResearchData(
    val unlocked: MutableSet<Identifier> = LinkedHashSet(),
    val favorites: MutableMap<Identifier, Int> = LinkedHashMap(),
    var bookLevel: Identifier? = null,
    var viewState: BookViewState = BookViewState(),
    var lastSyncedProgress: Map<Identifier, List<ResearchTaskProgress>> = emptyMap()
)

data class BookViewState(
    val category: Identifier? = null,
    val entry: Identifier? = null,
    val spread: Int = 0,
    val panX: Float = 0f,
    val panY: Float = 0f,
    val zoom: Float = 1f
)

class ResearchSavedData private constructor(
    private val players: MutableMap<UUID, PlayerResearchData>
) : SavedData() {
    constructor() : this(LinkedHashMap())

    fun player(uuid: UUID): PlayerResearchData = players.getOrPut(uuid, ::PlayerResearchData)

    private fun encode(): String = researchJson.encodeToString(
        ResearchStoreDto(players.mapKeys { it.key.toString() }.mapValues { it.value.toDto() })
    )

    companion object {
        private val CODEC: Codec<ResearchSavedData> = Codec.STRING.xmap(::decode, ResearchSavedData::encode)
        @JvmField val TYPE = SavedDataType("research_progress".ecRL, ::ResearchSavedData, CODEC, DataFixTypes.LEVEL)

        private fun decode(json: String): ResearchSavedData {
            if (json.isBlank()) return ResearchSavedData(LinkedHashMap())
            val root = researchJson.parseToJsonElement(json).jsonObject
            val store = if ("players" in root) {
                researchJson.decodeFromJsonElement<ResearchStoreDto>(root)
            } else {
                ResearchStoreDto(researchJson.decodeFromJsonElement<Map<String, PlayerResearchDataDto>>(root))
            }
            val players = store.players
                .mapKeysTo(LinkedHashMap()) { UUID.fromString(it.key) }
                .mapValuesTo(LinkedHashMap()) { it.value.toModel() }
            return ResearchSavedData(players)
        }
    }
}

@Serializable
private data class ResearchStoreDto(val players: Map<String, PlayerResearchDataDto> = emptyMap())

@Serializable
private data class PlayerResearchDataDto(
    val unlocked: Set<String> = emptySet(),
    val favorites: Map<String, Int> = emptyMap(),
    val bookLevel: String? = null,
    val viewState: BookViewStateDto = BookViewStateDto()
)

@Serializable
private data class BookViewStateDto(
    val category: String? = null,
    val entry: String? = null,
    val spread: Int = 0,
    val panX: Float = 0f,
    val panY: Float = 0f,
    val zoom: Float = 1f
)

private fun PlayerResearchData.toDto() = PlayerResearchDataDto(
    unlocked.mapTo(LinkedHashSet(), Identifier::toString),
    favorites.mapKeys { it.key.toString() },
    bookLevel?.toString(),
    viewState.toDto()
)

private fun PlayerResearchDataDto.toModel() = PlayerResearchData(
    unlocked.mapTo(LinkedHashSet(), Identifier::parse),
    favorites.mapKeysTo(LinkedHashMap()) { Identifier.parse(it.key) },
    bookLevel?.let(Identifier::parse),
    viewState.toModel()
)

private fun BookViewState.toDto() = BookViewStateDto(category?.toString(), entry?.toString(), spread, panX, panY, zoom)
private fun BookViewStateDto.toModel() = BookViewState(
    category?.let(Identifier::parse),
    entry?.let(Identifier::parse),
    spread.coerceAtLeast(0),
    panX.takeIf { it.isFinite() } ?: 0f,
    panY.takeIf { it.isFinite() } ?: 0f,
    zoom.takeIf { it.isFinite() }?.coerceIn(0.5f, 2f) ?: 1f
)

object ResearchProgress {
    @JvmStatic
    fun data(player: ServerPlayer): PlayerResearchData = storage(player.level().server).player(player.uuid)

    @JvmStatic
    fun has(player: ServerPlayer, research: Identifier): Boolean = research in data(player).unlocked

    @JvmStatic
    fun available(player: ServerPlayer, entry: BookEntry): Boolean {
        val category = ResearchCatalog.snapshot().layout[entry.id]
            ?.category
            ?.let(ResearchCatalog.snapshot().categories::get)
            ?: return false
        return categoryAvailable(player, category) && entry.dependencies.all { has(player, it) }
    }

    @JvmStatic
    fun categoryAvailable(player: ServerPlayer, category: BookCategory): Boolean {
        val playerData = data(player)
        return category.dependencies.all { it in playerData.unlocked } && meetsBookLevel(playerData.bookLevel, category.bookLevel)
    }

    @JvmStatic
    fun setBookLevel(player: ServerPlayer, level: Identifier): Boolean {
        if (!ECRegistries.BOOK_LEVEL.containsKey(level)) return false
        val playerData = data(player)
        playerData.bookLevel = level
        storage(player.level().server).setDirty()
        sync(player)
        return true
    }

    @JvmStatic
    fun updateView(player: ServerPlayer, state: BookViewState) {
        val playerData = data(player)
        val category = state.category?.takeIf { it in ResearchCatalog.snapshot().categories }
        val entry = state.entry?.takeIf { it in playerData.unlocked && it in ResearchCatalog.snapshot().entries }
        playerData.viewState = state.copy(
            category = category,
            entry = entry,
            spread = state.spread.coerceAtLeast(0),
            panX = state.panX.takeIf { it.isFinite() } ?: 0f,
            panY = state.panY.takeIf { it.isFinite() } ?: 0f,
            zoom = state.zoom.takeIf { it.isFinite() }?.coerceIn(0.5f, 2f) ?: 1f
        )
        storage(player.level().server).setDirty()
    }

    @JvmStatic
    fun tryUnlock(player: ServerPlayer, research: Identifier): Boolean {
        val entry = ResearchCatalog.snapshot().entries[research] ?: return false
        val playerData = data(player)
        if (research in playerData.unlocked || !available(player, entry)) return false
        if (entry.tasks.any { !it.progress(player).complete }) return false
        entry.tasks.forEach { it.consume(player) }
        playerData.unlocked += research
        storage(player.level().server).setDirty()
        unlockAutomatic(player)
        sync(player)
        return true
    }

    @JvmStatic
    fun unlockAutomatic(player: ServerPlayer): Boolean {
        val playerData = data(player)
        var changed: Boolean
        var anyChanged = false
        do {
            changed = false
            ResearchCatalog.snapshot().entries.values.forEach { entry ->
                if (entry.automatic && entry.id !in playerData.unlocked && available(player, entry) && entry.tasks.all { it.progress(player).complete }) {
                    entry.tasks.forEach { it.consume(player) }
                    playerData.unlocked += entry.id
                    changed = true
                    anyChanged = true
                }
            }
        } while (changed)
        if (anyChanged) storage(player.level().server).setDirty()
        return anyChanged
    }

    @JvmStatic
    fun setFavorite(player: ServerPlayer, research: Identifier, color: Int?) {
        val playerData = data(player)
        if (research !in playerData.unlocked) return
        if (color == null) playerData.favorites.remove(research) else playerData.favorites[research] = color
        storage(player.level().server).setDirty()
        sync(player)
    }

    @JvmStatic
    fun onPlayerJoin(player: ServerPlayer) {
        val playerData = data(player)
        if (playerData.bookLevel == null) {
            playerData.bookLevel = defaultBookLevel()
            storage(player.level().server).setDirty()
        }
        unlockAutomatic(player)
        sync(player)
    }

    @JvmStatic
    fun tick(player: ServerPlayer) {
        if (player.level().gameTime % 20L != 0L) return
        if (unlockAutomatic(player)) sync(player) else syncProgress(player)
    }

    @JvmStatic
    fun sync(player: ServerPlayer) {
        val playerData = data(player)
        val progress = collectProgress(player)
        playerData.lastSyncedProgress = progress
        ResearchNetwork.sendToPlayer(
            player,
            ResearchSyncPayload(
                ResearchCatalog.exportJson(),
                playerData.unlocked,
                playerData.favorites,
                progress,
                playerData.bookLevel,
                playerData.viewState
            )
        )
    }

    @JvmStatic
    fun syncProgress(player: ServerPlayer) {
        val playerData = data(player)
        val progress = collectProgress(player)
        if (progress == playerData.lastSyncedProgress) return
        playerData.lastSyncedProgress = progress
        ResearchNetwork.sendProgressToPlayer(player, ResearchProgressPayload(progress))
    }

    @JvmStatic
    fun syncAll(server: MinecraftServer) {
        server.playerList.players.forEach(::sync)
    }

    private fun storage(server: MinecraftServer): ResearchSavedData = server.overworld().dataStorage.computeIfAbsent(ResearchSavedData.TYPE)

    private fun defaultBookLevel(): Identifier? = ECRegistries.BOOK_LEVEL.entrySet()
        .minByOrNull { it.value.order }
        ?.key
        ?.identifier()

    private fun collectProgress(player: ServerPlayer): Map<Identifier, List<ResearchTaskProgress>> =
        ResearchCatalog.snapshot().entries.values.associate { entry ->
            entry.id to entry.tasks.map { it.progress(player) }
        }

    internal fun meetsBookLevel(current: Identifier?, required: Identifier?): Boolean {
        if (required == null) return true
        val requiredLevel = ECRegistries.BOOK_LEVEL.getOptional(required).orElse(null) ?: return false
        val currentLevel = current?.let { ECRegistries.BOOK_LEVEL.getOptional(it).orElse(null) } ?: return false
        return currentLevel.order >= requiredLevel.order
    }
}
