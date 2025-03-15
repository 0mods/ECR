package team._0mods.ecr.api.mru

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.Level
import ru.hollowhorizon.hc.common.capabilities.containers.ItemStorage
import ru.hollowhorizon.hc.common.utils.rl
import team._0mods.ecr.api.item.BoundGem

/**
 * Represents an entity that can store, receive, or transfer MRU (Magical Radiation Units).
 *
 * This interface defines the core behavior for MRU holders, including storage access,
 * locator data for slot-based MRU interactions, and the specific type of MRU functionality.
 *
 * Can be applied to:
 *
 * [net.minecraft.world.level.block.entity.BlockEntity]
 */
interface MRUHolder {
    /**
     * Returns the current MRU storage associated with this holder.
     *
     * @return an instance of [MRUStorage] representing the stored MRU.
     */
    val mruContainer: MRUStorage

    /**
     * Provides locator data used for slot-based MRU access.
     *
     * If `null`, this holder cannot receive MRU from a generator.
     *
     * @return an optional [LocatorData] instance, or `null` if unavailable.
     */
    val locator: LocatorData? get() = null

    /**
     * Specifies the type of MRU functionality this holder provides.
     *
     * This is necessary for the MRU pick-up and distribution process.
     *
     * @return the [MRUHolderType] of this holder.
     */
    val holderType: MRUHolderType

    /**
     * Enum representing the different types of MRU Holders and their capabilities.
     */
    enum class MRUHolderType {
        /**
         * Represents an entity that can receive MRU but does not export it.
         */
        RECEIVER,

        /**
         * Represents an entity that can both receive and export MRU, acting as an intermediary.
         */
        TRANSLATOR,

        /**
         * Represents an entity that can both send and receive MRU universally.
         */
        IO;

        /**
         * Determines if this holder is capable of exporting MRU.
         *
         * @return `true` if this type is either [TRANSLATOR] or [IO], otherwise `false`.
         */
        val isExporter: Boolean get() = this == TRANSLATOR || this.isUniversal

        /**
         * Determines if this holder type supports both importing and exporting MRU.
         *
         * @return `true` if this type is [IO], otherwise `false`.
         */
        val isUniversal: Boolean get() = this == IO

        /**
         * Determines if this holder is capable of receiving MRU.
         *
         * @return `true` if this type is either [RECEIVER] or [IO], otherwise `false`.
         */
        val isReceiver: Boolean get() = this == RECEIVER || this.isUniversal
    }

    /**
     * Data structure containing locator storage information.
     *
     * @property locatorStorage the item storage associated with this locator.
     * @property locatorSlot the slot index within the storage.
     */
    data class LocatorData(val locatorStorage: ItemStorage, val locatorSlot: Int)
}

/**
 * Processes the reception of MRU from an external source.
 *
 * This method checks if the holder can receive MRU from a bound gem, locates the corresponding
 * MRU exporter, and transfers the appropriate amount of MRU asynchronously.
 *
 * @param level the current game level in which the MRU transfer is being processed.
 */
@OptIn(DelicateCoroutinesApi::class)
fun MRUHolder.processReceive(level: Level) {
    if (level.isClientSide) return

    val stack = this.locator?.let { it.locatorStorage.items.getItem(it.locatorSlot) } ?: return
    val item = stack.item as? BoundGem ?: return

    val pos = item.getBoundPos(stack) ?: return
    val server = level.server ?: return
    val world = item.getBoundedWorld(stack)

    val logicalLevel = world?.let { server.getLevel(ResourceKey.create(Registries.DIMENSION, it.rl)) } ?: level
    val exporterBlockEntity = logicalLevel.getBlockEntity(pos) as? MRUHolder ?: return

    if (!exporterBlockEntity.holderType.isExporter) return
    if (!this.holderType.isReceiver) return

    val currentContainer = this.mruContainer
    val generator = exporterBlockEntity.mruContainer

    if (!currentContainer.comparableWith(generator)) return

    GlobalScope.launch {
        val transferCount = item.transferStrength.reversedArray()
        transferCount.forEach { if (generator.canExtractAndReceive(currentContainer, it)) return@launch }
    }
}
