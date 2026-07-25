package com.algorithmlx.ecr.common.block.entity

import com.algorithmlx.ecr.api.block.entity.SynchronizedBlockEntity
import com.algorithmlx.ecr.api.multiblock.MultiblockPlacement
import com.algorithmlx.ecr.api.mru.MRUDevice
import com.algorithmlx.ecr.api.mru.storage.IOMRUStorage
import com.algorithmlx.ecr.api.mru.storage.MRUStorageContainer
import com.algorithmlx.ecr.common.block.EnrichmentChamberController
import com.algorithmlx.ecr.common.init.config.ECConfig
import com.algorithmlx.ecr.registry.BlockEntityTypeRegistry
import com.algorithmlx.ecr.registry.BlockRegistry
import com.algorithmlx.ecr.registry.MRUTypeRegistry
import com.algorithmlx.ecr.registry.MultiblockRegistry
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput

class EnrichmentChamberControllerEntity(worldPosition: BlockPos, blockState: BlockState): SynchronizedBlockEntity(
    BlockEntityTypeRegistry.instance.enrichmentChamberController, worldPosition, blockState
), MRUDevice {
    var mutableMRUStorage = MRUStorageContainer(0, MRUTypeRegistry.instance.radiationUnit) {
        this.setChanged()
    }
    private var placement: MultiblockPlacement? = null

    override fun loadAdditional(input: ValueInput) {
        placement = null

        val capacity = input.getIntOr(MRU_CAPACITY_TAG, 0).coerceAtLeast(0)
        mutableMRUStorage = mutableMRUStorage.copy(mruCapacity = capacity)
        this.mutableMRUStorage.load(input)

        if (input.getBooleanOr(HAS_PLACEMENT_TAG, false)) {
            val direction = Direction.entries.getOrNull(input.getIntOr(PLACEMENT_DIRECTION_TAG, -1))
            if (direction != null && direction.axis.isHorizontal) {
                placement = MultiblockPlacement(
                    blockPos,
                    direction,
                    input.getIntOr(PLACEMENT_START_X_TAG, 0),
                    input.getIntOr(PLACEMENT_START_Y_TAG, 0),
                    input.getIntOr(PLACEMENT_START_Z_TAG, 0),
                    input.getIntOr(PLACEMENT_VARIANT_TAG, -1)
                )
            }
        }

        super.loadAdditional(input)
    }

    override fun saveAdditional(output: ValueOutput) {
        output.putInt(MRU_CAPACITY_TAG, mutableMRUStorage.mruCapacity)
        this.mutableMRUStorage.save(output)

        val placement = placement
        output.putBoolean(HAS_PLACEMENT_TAG, placement != null)
        if (placement != null) {
            output.putInt(PLACEMENT_DIRECTION_TAG, placement.direction.ordinal)
            output.putInt(PLACEMENT_START_X_TAG, placement.startX)
            output.putInt(PLACEMENT_START_Y_TAG, placement.startY)
            output.putInt(PLACEMENT_START_Z_TAG, placement.startZ)
            output.putInt(PLACEMENT_VARIANT_TAG, placement.variantIndex)
        }

        super.saveAdditional(output)
    }

    override val mruStorage: IOMRUStorage get() = mutableMRUStorage
    override val deviceType: MRUDevice.DeviceType = MRUDevice.DeviceType.UNCONNECTABLE

    fun setPlacement(placement: MultiblockPlacement?) {
        if (this.placement == placement) {
            if (placement == null) updateCapacity(0)
            return
        }

        this.placement = placement

        if (placement == null) updateCapacity(0)
        setChanged()
    }

    private fun updateCapacity(capacity: Int) {
        if (capacity == mutableMRUStorage.mruCapacity) return

        val stored = mutableMRUStorage.mru
        mutableMRUStorage = mutableMRUStorage.copy(mruCapacity = capacity)
        mutableMRUStorage.set(stored)
        setChanged()
    }

    companion object {
        private val config = ECConfig.instance.enrichmentChamber
        private val multiblock = MultiblockRegistry.instance.enrichmentChamber
        private const val MRU_CAPACITY_TAG = "mru_capacity"
        private const val HAS_PLACEMENT_TAG = "has_multiblock_placement"
        private const val PLACEMENT_DIRECTION_TAG = "multiblock_direction"
        private const val PLACEMENT_START_X_TAG = "multiblock_start_x"
        private const val PLACEMENT_START_Y_TAG = "multiblock_start_y"
        private const val PLACEMENT_START_Z_TAG = "multiblock_start_z"
        private const val PLACEMENT_VARIANT_TAG = "multiblock_variant"
        private const val STRUCTURE_CHECK_INTERVAL = 20L

        @JvmStatic
        fun onTick(level: Level, state: BlockState, be: EnrichmentChamberControllerEntity) {
            if (level.isClientSide) return
            if (
                level.gameTime % STRUCTURE_CHECK_INTERVAL !=
                Math.floorMod(be.blockPos.asLong(), STRUCTURE_CHECK_INTERVAL)
            ) return

            val placement = EnrichmentChamberController.findPlacement(level, be.blockPos, state)
            be.setPlacement(placement)

            val active = placement != null
            if (state.getValue(EnrichmentChamberController.ACTIVE) != active) {
                level.setBlockAndUpdate(
                    be.blockPos,
                    state.setValue(EnrichmentChamberController.ACTIVE, active)
                )
            }

            if (placement == null) return

            val holderCount = multiblock.countMatchesIn(
                level,
                placement,
                BlockRegistry.instance.enrichmentChamberHolder
            )
            val capacity = (
                config.controllerCapacity.toLong() +
                    config.holderCapacity.toLong() * holderCount
                ).coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
            be.updateCapacity(capacity)
        }
    }
}
