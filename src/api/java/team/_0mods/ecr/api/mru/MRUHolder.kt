package team._0mods.ecr.api.mru

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.Level
import ru.hollowhorizon.hc.client.utils.rl
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
        RECEIVER, TRANSLATOR, IO;

        val isExporter: Boolean get() = this == TRANSLATOR || this.isUniversal

        val isUniversal: Boolean get() = this == IO

        val isReceiver: Boolean get() = this == RECEIVER || this.isUniversal
    }

    data class LocatorData(val locatorStorage: ItemStorage, val locatorSlot: Int)
}

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