package com.algorithmlx.ecr.common.block.entity

import com.algorithmlx.ecr.api.block.entity.SynchronizedContainerBlockEntity
import com.algorithmlx.ecr.api.mru.MRUDevice
import com.algorithmlx.ecr.api.mru.balance.MRUBalanceContainer
import com.algorithmlx.ecr.api.mru.balance.MutableMRUBalance
import com.algorithmlx.ecr.api.mru.loadMRUData
import com.algorithmlx.ecr.api.mru.saveMRUData
import com.algorithmlx.ecr.api.mru.storage.IOMRUStorage
import com.algorithmlx.ecr.api.mru.storage.MRUStorageContainer
import com.algorithmlx.ecr.common.block.HeatGenerator
import com.algorithmlx.ecr.common.init.config.ECConfig
import com.algorithmlx.ecr.registry.BlockEntityTypeRegistry
import com.algorithmlx.ecr.registry.MRUTypeRegistry
import net.minecraft.core.BlockPos
import net.minecraft.core.NonNullList
import net.minecraft.network.chat.Component
import net.minecraft.world.ContainerHelper
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput

class HeatGeneratorEntity(
    worldPosition: BlockPos,
    blockState: BlockState,
): SynchronizedContainerBlockEntity(
    BlockEntityTypeRegistry.instance.heatGenerator,
    worldPosition,
    blockState,
), MRUDevice {
    private var mutableMRUStorage = MRUStorageContainer(
        ECConfig.instance.heatGenerator.capacity,
        MRUTypeRegistry.instance.radiationUnit,
    ) { this.setChanged() }
    private var items: NonNullList<ItemStack> = NonNullList.withSize(2, ItemStack.EMPTY)
    private var upgraded = blockState.getValue(HeatGenerator.IS_UPGRADED)

    var isUpgraded: Boolean
        get() = this.upgraded
        set(value) {
            if (this.upgraded == value && this.blockState.getValue(HeatGenerator.IS_UPGRADED) == value) return
            this.upgraded = value

            val state = this.blockState.setValue(HeatGenerator.IS_UPGRADED, value)
            if (state != this.blockState) this.level?.setBlock(this.blockPos, state, Block.UPDATE_ALL)

            this.setChanged()
        }

    override fun saveAdditional(output: ValueOutput) {
        output.putBoolean(IS_UPGRADED_TAG, this.isUpgraded)
        ContainerHelper.saveAllItems(output, this.items)
        this.saveMRUData(output)
        super.saveAdditional(output)
    }

    override fun loadAdditional(input: ValueInput) {
        this.upgraded = input.getBooleanOr(IS_UPGRADED_TAG, this.blockState.getValue(HeatGenerator.IS_UPGRADED))
        ContainerHelper.loadAllItems(input, this.items)
        this.loadMRUData(input)
        super.loadAdditional(input)
    }

    override fun getDefaultName(): Component = Component.empty()

    override fun getItems(): NonNullList<ItemStack> = this.items

    override fun setItems(items: NonNullList<ItemStack>) {
        this.items = items
    }

    override fun createMenu(
        containerId: Int,
        inventory: Inventory,
    ): AbstractContainerMenu {
        TODO("Not yet implemented")
    }

    override fun getContainerSize(): Int = this.items.size

    override val mruStorage: IOMRUStorage
        get() = this.mutableMRUStorage
    override val balance: MutableMRUBalance = MRUBalanceContainer { setChanged() }
    override val deviceType: MRUDevice.DeviceType = MRUDevice.DeviceType.TRANSLATOR

    companion object {
        const val IS_UPGRADED_TAG = "is_upgraded"
    }
}
