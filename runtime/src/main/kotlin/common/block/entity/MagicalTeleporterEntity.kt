package com.algorithmlx.ecr.common.block.entity

import com.algorithmlx.ecr.api.mru.MRUDevice
import com.algorithmlx.ecr.api.mru.storage.IOMRUStorage
import com.algorithmlx.ecr.api.mru.storage.MRUStorageContainer
import com.algorithmlx.ecr.api.block.entity.SynchronizedContainerBlockEntity
import com.algorithmlx.ecr.api.item.BoundGem
import com.algorithmlx.ecr.api.mru.processReceive
import com.algorithmlx.ecr.common.api.BoundGemHelper
import com.algorithmlx.ecr.common.init.config.ECConfig
import com.algorithmlx.ecr.common.menu.MagicalTeleporterMenu
import com.algorithmlx.ecr.registry.*
import net.minecraft.core.BlockPos
import net.minecraft.core.NonNullList
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.ContainerHelper
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ContainerLevelAccess
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.EndPortalBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.portal.TeleportTransition
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput

class MagicalTeleporterEntity(
    worldPosition: BlockPos, blockState: BlockState
): SynchronizedContainerBlockEntity(BlockEntityTypeRegistry.instance.magicalTeleporter, worldPosition, blockState), MRUDevice {
    private var items: NonNullList<ItemStack> = NonNullList.withSize(2, ItemStack.EMPTY)
    private var progressTime = 0

    var structureIsValid = false

    override fun getDefaultName(): Component = Component.empty()

    override fun getItems(): NonNullList<ItemStack> = this.items

    override fun setItems(items: NonNullList<ItemStack>) {
        this.items = items
    }

    override fun createMenu(
        containerId: Int,
        inventory: Inventory
    ): AbstractContainerMenu = MagicalTeleporterMenu(
        containerId, inventory,
        this, this,
        ContainerLevelAccess.create(this.level!!, this.blockPos)
    )

    override fun saveAdditional(output: ValueOutput) {
        ContainerHelper.saveAllItems(output, this.items)

        output.putInt("progress", this.progressTime)
        output.putBoolean("structure_valid", this.structureIsValid)

        mruStorage.save(output)
        super.saveAdditional(output)
    }

    override fun loadAdditional(input: ValueInput) {
        ContainerHelper.loadAllItems(input, this.items)

        this.progressTime = input.getIntOr("progress", 0)
        this.structureIsValid = input.getBooleanOr("structure_valid", false)

        mruStorage.load(input)
        super.loadAdditional(input)
    }

    override fun getContainerSize(): Int = this.items.size
    override val mruStorage: IOMRUStorage = MRUStorageContainer(50000, MRUTypeRegistry.instance.radiationUnit) { this.setChanged() }
    override val deviceType: MRUDevice.DeviceType = MRUDevice.DeviceType.CONNECTABLE_RECEIVER
    override val locator: MRUDevice.LocatorData = MRUDevice.LocatorData(this, 0)

    companion object {
        private val config = ECConfig.current.magicalTeleporter

        @JvmStatic
        fun hasValidStructure(level: Level, pos: BlockPos): Boolean =
            MultiblockRegistry.instance.magicalTeleporter.findPlacement(level, pos, BlockPos(2, 0, 2)) != null

        @JvmStatic
        fun onTick(level: Level, pos: BlockPos, blockEntity: MagicalTeleporterEntity) {
            val oldValid = blockEntity.structureIsValid
            val newValid = hasValidStructure(level, pos)

            if (oldValid != newValid) {
                blockEntity.structureIsValid = newValid
                blockEntity.setChanged()
            }

            if (level.isClientSide) return

            if (!blockEntity.structureIsValid) {
                blockEntity.resetProgress()
                return
            }

            blockEntity.processReceive(level)

            val stack = blockEntity.getItem(1)
            if (stack.item !is BoundGem) return

            val destPos = BoundGemHelper.getBoundPos(stack) ?: return
            val levelKey = BoundGemHelper.getLevelKey(stack)

            val dimensionalLevel = levelKey?.let { (level as ServerLevel).server.getLevel(it) } ?: level

            if (pos == destPos) {
                blockEntity.resetProgress()
                return
            }

            val entityAtTeleporter = level.getNearestPlayer(pos.x + 0.5, pos.y + 1.0, pos.z + 0.5, 1.0, false) ?: return

            val destOpt = dimensionalLevel.getBlockEntity(destPos, BlockEntityTypeRegistry.instance.magicalTeleporter)
            if (!destOpt.isPresent) {
                blockEntity.resetProgress()
                return
            }

            val dest = destOpt.get()
            if (!dest.structureIsValid) return

            if (blockEntity.mruStorage.canExtract(config.mruUsage)) {
                blockEntity.mruStorage.extract(config.mruUsage)
                blockEntity.progressTime++
                blockEntity.setChanged()
            }

            if (blockEntity.progressTime < config.ticksRequired) return

            entityAtTeleporter.teleportTo(
                dimensionalLevel as ServerLevel,
                dest.blockPos.x + 0.5,
                dest.blockPos.y + 1.0,
                dest.blockPos.z + 0.5,
                setOf(),
                entityAtTeleporter.yRot,
                entityAtTeleporter.xRot,
                false
            )

            blockEntity.resetProgress()
        }

        private fun MagicalTeleporterEntity.resetProgress() {
            this.progressTime = 0
            this.setChanged()
        }
    }
}
