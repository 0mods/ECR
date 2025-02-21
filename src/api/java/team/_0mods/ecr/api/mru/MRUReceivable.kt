@file:JvmName("MRUReceiveUtils")
package team._0mods.ecr.api.mru

import kotlinx.coroutines.*
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

@OptIn(DelicateCoroutinesApi::class)
fun MRUReceivable.processReceive(level: Level) {
    if (level.isClientSide) return

    val stack = positionCrystal ?: return
    val item = stack.item as? BoundGem ?: return
    val pos = item.getBoundPos(stack) ?: return
    val server = level.server ?: return
    val world = item.getBoundedWorld(stack)
    val dimensionalLevel = world?.let { server.getLevel(ResourceKey.create(Registries.DIMENSION, it.rl)) }

    val blockEntity = (dimensionalLevel ?: level).getBlockEntity(pos) as? MRUGenerator ?: return
    val currentContainer = mruContainer
    val generator = blockEntity.currentMRUStorage

    GlobalScope.launch(Dispatchers.Default) {
        val amounts = listOf(1000, 100, 50, 10, 1)
        amounts.forEach { if (generator.checkExtractAndReceive(currentContainer, it)) return@forEach }
    }
}
