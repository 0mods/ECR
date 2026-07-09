package com.algorithmlx.ecr.common.block.entity

import com.algorithmlx.ecr.api.mru.MRUHolder
import com.algorithmlx.ecr.api.mru.storage.IOMRUStorage
import com.algorithmlx.ecr.api.mru.storage.MRUStorageContainer
import com.algorithmlx.ecr.common.init.registry.BlockEntityTypeRegistry
import com.algorithmlx.ecr.common.init.registry.MRUTypeRegistry
import com.algorithmlx.ecr.common.menu.EnvoyerMenu
import net.minecraft.core.BlockPos
import net.minecraft.core.NonNullList
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ContainerData
import net.minecraft.world.inventory.ContainerLevelAccess
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput

class EnvoyerBlockEntity(
    worldPosition: BlockPos,
    blockState: BlockState
) : BaseContainerBlockEntity(BlockEntityTypeRegistry.instance.envoyer, worldPosition, blockState), MRUHolder {
    private var items: NonNullList<ItemStack> = NonNullList.withSize(8, ItemStack.EMPTY)

    private val containerData: ContainerData = object : ContainerData {
        override fun get(index: Int): Int  = when (index) {
            0 -> this@EnvoyerBlockEntity.progress
            1 -> this@EnvoyerBlockEntity.maxProgress
            else -> 0
        }

        override fun set(index: Int, value: Int) {
            when (index) {
                0 -> this@EnvoyerBlockEntity.progress = value
                1 -> this@EnvoyerBlockEntity.maxProgress = value
            }
        }

        override fun getCount(): Int = 2
    }

    var progress = 0
    var maxProgress = 0

    override fun getDefaultName(): Component = Component.empty()

    override fun getItems(): NonNullList<ItemStack> = this.items

    override fun setItems(items: NonNullList<ItemStack>) {
        this.items = items
    }

    override fun createMenu(
        containerId: Int,
        inventory: Inventory
    ): AbstractContainerMenu = EnvoyerMenu(
        containerId, inventory, this, this,
        ContainerLevelAccess.create(this.level!!, this.blockPos),
        containerData
    )

    override fun saveAdditional(output: ValueOutput) {
        output.putInt("progress", this.progress)
        output.putInt("max_progress", this.maxProgress)
        this.mruStorage.save(output)
        super.saveAdditional(output)
    }

    override fun loadAdditional(input: ValueInput) {
        this.progress = input.getIntOr("progress", 0)
        this.maxProgress = input.getIntOr("max_progress", 0)
        this.mruStorage.load(input)
        super.loadAdditional(input)
    }

    override fun getContainerSize(): Int = 8

    override fun canPlaceItem(slot: Int, itemStack: ItemStack): Boolean = if (slot == 5) false else super.canPlaceItem(slot, itemStack)

    override val mruStorage: IOMRUStorage = MRUStorageContainer(5000, MRUTypeRegistry.instance.espe) { setChanged() }
    override val holderType: MRUHolder.MRUHolderType = MRUHolder.MRUHolderType.RECEIVER

    override val locator: MRUHolder.LocatorData = MRUHolder.LocatorData(this, 6)

    companion object {
        fun onTick(level: Level, be: EnvoyerBlockEntity) {
            if (level.isClientSide) return
            be.processReceive(level)
            be.processRecipeIfPresent(level)
        }

        private fun EnvoyerBlockEntity.processRecipeIfPresent(level: Level) {}

        private fun EnvoyerBlockEntity.processReceive(level: Level) {}
    }
}
