package com.algorithmlx.ecr.common.block.entity

import com.algorithmlx.ecr.api.mru.MRUHolder
import com.algorithmlx.ecr.common.components.MRUStorageComponent
import net.minecraft.core.BlockPos
import net.minecraft.core.NonNullList
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ContainerData
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput

class MithrilineFurnaceEntity(
    type: BlockEntityType<*>,
    worldPosition: BlockPos,
    blockState: BlockState
): BaseContainerBlockEntity(type, worldPosition, blockState), MRUHolder {
    private val containerData: ContainerData = object : ContainerData {
        override fun get(index: Int): Int = when (index) {
            0 -> this@MithrilineFurnaceEntity.craftProgress
            1 -> this@MithrilineFurnaceEntity.maxCraftProgress
            else -> 0
        }

        override fun set(index: Int, value: Int) {
            when (index) {
                0 -> this@MithrilineFurnaceEntity.craftProgress = value
                1 -> this@MithrilineFurnaceEntity.maxCraftProgress = value
            }
        }

        override fun getCount(): Int = 2
    }

    var structureIsValid = false
    var receivingTicks = 0
    var craftProgress = 0
    var maxCraftProgress = 0
    var slownessGeneration = false
    // Client Only
    var coreRotationPrevious = 0f
    var coreRotationAngle = 0f

    override fun saveAdditional(output: ValueOutput) {
        output.putBoolean("structure_valid", structureIsValid)
        output.putBoolean("slow_generation", this.slownessGeneration)
        output.putInt("progress", this.craftProgress)
        output.putInt("max_progress", this.maxCraftProgress)
        mruContainer.save(output)
        super.saveAdditional(output)
    }

    override fun loadAdditional(input: ValueInput) {
        structureIsValid = input.getBooleanOr("structure_valid", false)
        slownessGeneration = input.getBooleanOr("slow_generation", false)
        craftProgress = input.getIntOr("progress", 0)
        maxCraftProgress = input.getIntOr("max_progress", 0)
        mruContainer.load(input)
        super.loadAdditional(input)
    }

    override fun getDefaultName(): Component {
        TODO("Not yet implemented")
    }

    override fun getItems(): NonNullList<ItemStack> {
        TODO("Not yet implemented")
    }

    override fun setItems(items: NonNullList<ItemStack>) {
        TODO("Not yet implemented")
    }

    override fun createMenu(
        containerId: Int,
        inventory: Inventory
    ): AbstractContainerMenu {
        TODO("Not yet implemented")
    }

    override fun getContainerSize(): Int = 2

    override val mruContainer: MRUStorageComponent = MRUStorageComponent(10000, TODO("Not yet implemented"))
    override val holderType: MRUHolder.MRUHolderType = MRUHolder.MRUHolderType.RECEIVER
}
