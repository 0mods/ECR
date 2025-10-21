package team._0mods.ecr.common.blocks.entity

import net.minecraft.core.BlockPos
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.tags.BlockTags
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import ru.hollowhorizon.hc.common.capabilities.CapabilityInstance
import ru.hollowhorizon.hc.common.capabilities.HollowCapability
import ru.hollowhorizon.hc.common.objects.blocks.HollowBlockEntity
import ru.hollowhorizon.hc.common.utils.get
import team._0mods.ecr.api.mru.MRUHolder
import team._0mods.ecr.api.mru.MRUStorage
import team._0mods.ecr.api.mru.MRUTypes
import team._0mods.ecr.common.init.registry.ECRRegistry
import team._0mods.ecr.commonConfig

class ColdDistillerEntity(
    pos: BlockPos,
    blockState: BlockState
): HollowBlockEntity(ECRRegistry.coldDistillerEntity, pos, blockState), MRUHolder {
    override val mruContainer: MRUStorage = this[MRUContainer::class]
    override val holderType: MRUHolder.MRUHolderType = MRUHolder.MRUHolderType.TRANSLATOR

    companion object {
        private val coldDistillerConfig = commonConfig.coldDistiller
        @JvmStatic
        fun tick(level: Level, pos: BlockPos, state: BlockState, be: ColdDistillerEntity) {
            if (level.isClientSide) return
            val rad = radiusIceBlock(level, pos, coldDistillerConfig.findRadius)
            team._0mods.ecr.api.LOGGER.debug("blocks: ${rad.size}")
        }

        fun radiusIceBlock(level: Level, pos: BlockPos, radius: Int): List<BlockState> {
            val list = mutableListOf<BlockState>()
            for (x in -radius .. radius) {
                for (z in -radius .. radius) {
                    for (y in -radius .. radius) {
                        val state = level.getBlockState(BlockPos(pos.x + x, pos.y + y, pos.z + z))
                        val tag = BuiltInRegistries.BLOCK.getTag(BlockTags.ICE)
                        if (!tag.isPresent) continue
                        if (!tag.get().contains(state.blockHolder)) continue
                        list += state
                    }
                }
            }

            return list
        }
    }

    @HollowCapability(ColdDistillerEntity::class)
    class MRUContainer: CapabilityInstance(), MRUStorage {
        override var mru: Int by syncable(0)
        override val maxMRUStorage: Int = 100000
        override val mruType: MRUTypes = MRUTypes.RADIATION_UNIT
    }
}
