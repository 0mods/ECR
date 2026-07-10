package com.algorithmlx.ecr.common.block.entity

import com.algorithmlx.ecr.api.mru.MRUDevice
import com.algorithmlx.ecr.api.mru.storage.IOMRUStorage
import com.algorithmlx.ecr.api.mru.storage.MRUStorageContainer
import com.algorithmlx.ecr.common.init.registry.MRUTypeRegistry
import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.BlockPos
import net.minecraft.core.NonNullList
import net.minecraft.network.chat.Component
import net.minecraft.world.ContainerHelper
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput
import kotlin.jvm.optionals.getOrElse

class MatrixDestructorEntity(
    type: BlockEntityType<*>, worldPosition: BlockPos, blockState: BlockState
): BaseContainerBlockEntity(type, worldPosition, blockState), MRUDevice {
    private var items: NonNullList<ItemStack> = NonNullList.withSize(1, ItemStack.EMPTY)

    var progress = 0
    @JvmField
    var status = MatrixDestructorStatus.STOPPED

    override fun getDefaultName(): Component = Component.empty()

    override fun getItems(): NonNullList<ItemStack> = this.items

    override fun setItems(items: NonNullList<ItemStack>) {
        this.items = items
    }

    override fun saveAdditional(output: ValueOutput) {
        ContainerHelper.saveAllItems(output, this.items)
        output.store("status", MatrixDestructorStatus.CODEC, this.status)
        output.putInt("progress", this.progress)
        super.saveAdditional(output)
    }

    override fun loadAdditional(input: ValueInput) {
        ContainerHelper.loadAllItems(input, this.items)
        this.status = input.read("status", MatrixDestructorStatus.CODEC).getOrElse { MatrixDestructorStatus.STOPPED }
        this.progress = input.getIntOr("progress", 0)
        super.loadAdditional(input)
    }

    override fun createMenu(
        containerId: Int,
        inventory: Inventory
    ): AbstractContainerMenu {
        TODO("Not yet implemented")
    }

    override fun getContainerSize(): Int = 1

    override val mruStorage: IOMRUStorage = MRUStorageContainer(10000, MRUTypeRegistry.instance.radiationUnit)
    override val holderType: MRUDevice.DeviceType = MRUDevice.DeviceType.TRANSLATOR

    enum class MatrixDestructorStatus {
        WAITING, WORKING, WARNING, STOPPED;

        companion object {
            @JvmField
            val CODEC: Codec<MatrixDestructorStatus> = RecordCodecBuilder.create {
                it.group(
                    Codec.STRING.fieldOf("name").forGetter(MatrixDestructorStatus::name)
                ).apply(it, MatrixDestructorStatus::valueOf)
            }
        }
    }
}