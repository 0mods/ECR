package team._0mods.ecr.common.blocks.entity

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import ru.hollowhorizon.hc.common.capabilities.CapabilityInstance
import ru.hollowhorizon.hc.common.capabilities.HollowCapability
import ru.hollowhorizon.hc.common.objects.blocks.HollowBlockEntity
import ru.hollowhorizon.hc.common.utils.get
import team._0mods.ecr.api.mru.MRUHolder
import team._0mods.ecr.api.mru.MRUStorage
import team._0mods.ecr.api.mru.MRUTypes

class ColdDistillerEntity(
    type: BlockEntityType<*>,
    pos: BlockPos,
    blockState: BlockState
): HollowBlockEntity(type, pos, blockState), MRUHolder {
    override val mruContainer: MRUStorage = this[MRUContainer::class]
    override val holderType: MRUHolder.MRUHolderType = MRUHolder.MRUHolderType.TRANSLATOR

    @HollowCapability(ColdDistillerEntity::class)
    class MRUContainer: CapabilityInstance(), MRUStorage {
        override var mru: Int by syncable(0)
        override val maxMRUStorage: Int = 100000
        override val mruType: MRUTypes = MRUTypes.RADIATION_UNIT
    }
}
