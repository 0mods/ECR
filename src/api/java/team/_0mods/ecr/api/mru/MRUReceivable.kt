@file:JvmName("MRUReceiveUtils")
package team._0mods.ecr.api.mru

import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import ru.hollowhorizon.hc.client.utils.rl
import team._0mods.ecr.api.item.BoundGem

interface MRUReceivable {
    /**
     * Gets [ItemStack] in Container that's item is instanceof [BoundGem]
     *
     * @return [ItemStack]
     */
    val positionCrystal: ItemStack

    /**
     * Gets the current [MRUContainer]
     *
     * @return [MRUContainer]
     */
    val mruContainer: MRUContainer
}

fun MRUReceivable.processReceive(level: Level) {
    if (level.isClientSide) return
    val item = this.positionCrystal.item
    if (item !is BoundGem) return
    val pos = item.getBoundPos(this.positionCrystal) ?: return
    val server = level.server ?: return
    val world = item.getBoundedWorld(this.positionCrystal)
    val dimensionalLevel = if (world != null)
        server.getLevel(ResourceKey.create(Registry.DIMENSION_REGISTRY, world.rl))
    else null

    val blockEntity = if (dimensionalLevel != null) dimensionalLevel.getBlockEntity(pos) else level.getBlockEntity(pos)

    if (blockEntity !is MRUGenerator) return

    val currentContainer = this.mruContainer
    val generator = blockEntity.currentMRUContainer

    if (!generator.checkExtractAndReceive(currentContainer, 100)) {
        if (!generator.checkExtractAndReceive(currentContainer, 50)) {
            if (!generator.checkExtractAndReceive(currentContainer, 10))
                generator.checkExtractAndReceive(currentContainer, 1)
        }
    }
}
