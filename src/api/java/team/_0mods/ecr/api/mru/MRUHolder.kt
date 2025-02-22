package team._0mods.ecr.api.mru

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import ru.hollowhorizon.hc.client.utils.rl
import team._0mods.ecr.api.LOGGER
import team._0mods.ecr.api.item.BoundGem

interface MRUHolder {
    /**
     * Gets the current [MRUStorage]
     *
     * @return [MRUStorage]
     */
    val mruContainer: MRUStorage

    /**
     * Gets [ItemStack] in Container that's item is instanceof [BoundGem].
     *
     * If null, MRU cannot be received from the generator
     *
     * @return [ItemStack] if present else `null` by default
     */
    val locator: ItemStack? get() = null

    val holderType: MRUHolderType

    enum class MRUHolderType {
        RECEIVER, TRANSLATOR, STORAGE, IO;

        val isExporter: Boolean get() = this == TRANSLATOR || this.isUniversal

        val isUniversal: Boolean get() = this == IO

        val isStorage: Boolean get() = this == STORAGE || this.isUniversal
    }
}

@OptIn(DelicateCoroutinesApi::class)
fun MRUHolder.processReceive(level: Level) {
    if (level.isClientSide) return
    LOGGER.info("Processing receive...")

    val stack = this.locator ?: return
    LOGGER.info("Stack was loaded")
    val item = stack.item as? BoundGem ?: return
    LOGGER.info("Stack is Bound Gem")
    val pos = item.getBoundPos(stack) ?: return
    LOGGER.info("Position: $pos")
    val server = level.server ?: return
    LOGGER.info("Server is loaded")
    val world = item.getBoundedWorld(stack)
    LOGGER.info("World was taken")

    val dimensionalLevel = world?.let { server.getLevel(ResourceKey.create(Registries.DIMENSION, world.rl)) }
    val blockEntity = ((dimensionalLevel ?: level).getBlockEntity(pos)) as? MRUHolder ?: return
    LOGGER.info("Block Entity founded")

    if (!blockEntity.holderType.isExporter) return
    LOGGER.info("Attached block is exporter")

    if (blockEntity.mruContainer.mruType != this.mruContainer.mruType) return
    LOGGER.info("Mru types was matched")

    val currentContainer = this.mruContainer
    val generator = blockEntity.mruContainer

    GlobalScope.launch {
        /*val transferCount = item.transferStrength.reversedArray()
        for (count in transferCount)
            if (generator.canExtractAndReceive(currentContainer, count)) break*/

        LOGGER.info("RECEIVING")
        if (!generator.canExtractAndReceive(currentContainer, 1000)) {
            if (!generator.canExtractAndReceive(currentContainer, 100)) {
                if (!generator.canExtractAndReceive(currentContainer, 50)) {
                    if (!generator.canExtractAndReceive(currentContainer, 10))
                        generator.canExtractAndReceive(currentContainer, 1)
                }
            }
        }
    }
}