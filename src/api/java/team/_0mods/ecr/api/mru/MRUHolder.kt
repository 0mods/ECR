package team._0mods.ecr.api.mru

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.Level
import ru.hollowhorizon.hc.client.utils.rl
import team._0mods.ecr.api.LOGGER
import team._0mods.ecr.api.item.BoundGem
import team._0mods.ecr.api.item.ItemStorage

interface MRUHolder {
    /**
     * Gets the current [MRUStorage]
     *
     * @return [MRUStorage]
     */
    val mruContainer: MRUStorage

    /**
     * Gets Locator Data Container for slot getting.
     *
     * If null, MRU cannot be received from the generator
     *
     * @return [LocatorData] if present else `null` by default
     */
    val locator: LocatorData? get() = null

    val holderType: MRUHolderType

    enum class MRUHolderType {
        RECEIVER, TRANSLATOR, STORAGE, IO;

        val isExporter: Boolean get() = this == TRANSLATOR || this.isUniversal

        val isUniversal: Boolean get() = this == IO

        val isStorage: Boolean get() = this == STORAGE || this.isUniversal
    }

    data class LocatorData(val locatorStorage: ItemStorage, val locatorSlot: Int)
}

@OptIn(DelicateCoroutinesApi::class)
fun MRUHolder.processReceive(level: Level) {
    if (level.isClientSide) return
    LOGGER.info("Processing receive...")

    val stack = this.locator?.let { it.locatorStorage.items.getItem(it.locatorSlot) } ?: return
    LOGGER.info("Stack was loaded")
    val item = stack.item as? BoundGem
    if (item == null) {
        LOGGER.info("It is not Bound Gem. Item: ${stack.item.descriptionId}")
        return
    }
    LOGGER.info("Stack is Bound Gem")
    val pos = item.getBoundPos(stack) ?: return
    LOGGER.info("Position: $pos")
    val server = level.server ?: return
    LOGGER.info("Server is loaded")
    val world = item.getBoundedWorld(stack)
    LOGGER.info("World was taken")

    val dimensionalLevel = world?.let { server.getLevel(ResourceKey.create(Registries.DIMENSION, world.rl)) }
    val exporterBlockEntity = ((dimensionalLevel ?: level).getBlockEntity(pos)) as? MRUHolder ?: return
    LOGGER.info("Block Entity founded")

    if (!exporterBlockEntity.holderType.isExporter) return
    LOGGER.info("Attached block is exporter")

    if (exporterBlockEntity.mruContainer.mruType != this.mruContainer.mruType) {
        LOGGER.warn("MRU Types are not matches!")
        LOGGER.warn("Provided: ${exporterBlockEntity.mruContainer.mruType.displayName.string}; Needed: ${this.mruContainer.mruType.displayName.string}")
        return
    }
    LOGGER.info("Mru types was matched")

    val currentContainer = this.mruContainer
    val generator = exporterBlockEntity.mruContainer

    GlobalScope.launch {
        val transferCount = item.transferStrength.reversedArray()
        for (count in transferCount)
            if (generator.canExtractAndReceive(currentContainer, count)) break
    }
}