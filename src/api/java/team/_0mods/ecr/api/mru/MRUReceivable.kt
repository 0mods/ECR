@file:JvmName("MRUReceiveUtils")
package team._0mods.ecr.api.mru

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import ru.hollowhorizon.hc.client.utils.rl
import team._0mods.ecr.api.item.BoundGem

interface MRUReceivable {
    /**
     * Gets [ItemStack] in Container that's item is instanceof [BoundGem].
     *
     * If null, MRU cannot be received from the generator
     *
     * @return [ItemStack] if present else `null` by default
     */
    val positionCrystal: ItemStack? get() = null

    /**
     * Gets the current [MRUStorage]
     *
     * @return [MRUStorage]
     */
    val mruContainer: MRUStorage
}

fun MRUReceivable.processReceive(level: Level) {
    val scope = CoroutineScope(Dispatchers.Default)
    if (level.isClientSide) return
    val stack = this.positionCrystal ?: return
    val item = stack.item
    if (item !is BoundGem) return
    val pos = item.getBoundPos(stack) ?: return
    val server = level.server ?: return
    val world = item.getBoundedWorld(stack)
    val dimensionalLevel = if (world != null)
        server.getLevel(ResourceKey.create(Registries.DIMENSION, world.rl))
    else null

    val blockEntity = if (dimensionalLevel != null) dimensionalLevel.getBlockEntity(pos) else level.getBlockEntity(pos)

    if (blockEntity !is MRUGenerator) return

    val currentContainer = this.mruContainer
    val generator = blockEntity.currentMRUStorage

    scope.launch {
        if (!generator.checkExtractAndReceive(currentContainer, 100)) {
            if (!generator.checkExtractAndReceive(currentContainer, 50)) {
                if (!generator.checkExtractAndReceive(currentContainer, 10))
                    generator.checkExtractAndReceive(currentContainer, 1)
            }
        }
    }
}
