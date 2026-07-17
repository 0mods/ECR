package com.algorithmlx.ecr.common.block.entity

import com.algorithmlx.ecr.api.mru.MRUDevice
import com.algorithmlx.ecr.api.mru.storage.IOMRUStorage
import com.algorithmlx.ecr.api.mru.storage.MRUStorageContainer
import com.algorithmlx.ecr.common.api.block.entity.SynchronizedBlockEntity
import com.algorithmlx.ecr.common.init.registry.BlockEntityTypeRegistry
import com.algorithmlx.ecr.common.init.registry.MRUTypeRegistry
import net.minecraft.core.BlockPos
import net.minecraft.tags.BlockTags
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState

class ColdDistillerEntity(worldPosition: BlockPos, blockState: BlockState): SynchronizedBlockEntity(
    BlockEntityTypeRegistry.instance.coldDistiller, worldPosition, blockState
), MRUDevice {
    override val mruStorage: IOMRUStorage = MRUStorageContainer(100000, MRUTypeRegistry.instance.radiationUnit) { setChanged() }
    override val deviceType: MRUDevice.DeviceType = MRUDevice.DeviceType.TRANSLATOR

    companion object {
        @JvmStatic
        fun onTick(level: Level, pos: BlockPos, entity: ColdDistillerEntity) {
            if (level.isClientSide) return
            val rad = radiusIceBlock(level, pos, 3)
            com.algorithmlx.ecr.api.LOGGER.info("blocks: ${rad.size}")
        }

        private fun radiusIceBlock(level: Level, pos: BlockPos, radius: Int): List<BlockState> {
            val list = mutableListOf<BlockState>()
            val rad = -radius .. radius

            rad.forEach { x ->
                rad.forEach { y ->
                    rad.forEach z@{ z ->
                        val state = level.getBlockState(BlockPos(pos.x + x, pos.y + y, pos.z + z))
                        if (!state.`is`(BlockTags.ICE)) return@z
                        list += state
                    }
                }
            }

            return list.toList()
        }
    }
}
