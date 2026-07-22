package com.algorithmlx.ecr.common.block.entity

import com.algorithmlx.ecr.api.mru.MRUDevice
import com.algorithmlx.ecr.api.mru.storage.IOMRUStorage
import com.algorithmlx.ecr.api.mru.storage.MRUStorageContainer
import com.algorithmlx.ecr.common.api.block.entity.SynchronizedContainerBlockEntity
import com.algorithmlx.ecr.common.init.config.ECConfig
import com.algorithmlx.ecr.registry.*
import net.minecraft.core.BlockPos
import net.minecraft.core.NonNullList
import net.minecraft.network.chat.Component
import net.minecraft.world.ContainerHelper
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput

class MagicalTeleporterEntity(
    worldPosition: BlockPos, blockState: BlockState
): SynchronizedContainerBlockEntity(BlockEntityTypeRegistry.instance.magicalTeleporter, worldPosition, blockState), MRUDevice {
    private var items: NonNullList<ItemStack> = NonNullList.withSize(2, ItemStack.EMPTY)
    private var progressTime = 0

    override fun getDefaultName(): Component = Component.empty()

    override fun getItems(): NonNullList<ItemStack> = this.items

    override fun setItems(items: NonNullList<ItemStack>) {
        this.items = items
    }

    override fun createMenu(
        containerId: Int,
        inventory: Inventory
    ): AbstractContainerMenu {
        TODO("Not yet implemented")
    }

    override fun saveAdditional(output: ValueOutput) {
        ContainerHelper.saveAllItems(output, this.items)
        output.putInt("progress", this.progressTime)
        mruStorage.save(output)
        super.saveAdditional(output)
    }

    override fun loadAdditional(input: ValueInput) {
        ContainerHelper.loadAllItems(input, this.items)
        this.progressTime = input.getIntOr("progress", 0)
        mruStorage.load(input)
        super.loadAdditional(input)
    }

    override fun getContainerSize(): Int = this.items.size
    override val mruStorage: IOMRUStorage = MRUStorageContainer(50000, MRUTypeRegistry.instance.radiationUnit) { this.setChanged() }
    override val deviceType: MRUDevice.DeviceType = MRUDevice.DeviceType.CONNECTABLE_RECEIVER

    companion object {
        val config = ECConfig.current.magicalTeleporter

        @JvmStatic
        fun onTick(level: Level, blockEntity: MagicalTeleporterEntity) {}
    }
}
