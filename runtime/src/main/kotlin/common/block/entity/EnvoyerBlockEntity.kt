package com.algorithmlx.ecr.common.block.entity

import com.algorithmlx.ecr.api.mru.MRUDevice
import com.algorithmlx.ecr.api.mru.processReceive
import com.algorithmlx.ecr.api.mru.storage.IOMRUStorage
import com.algorithmlx.ecr.api.mru.storage.MRUStorageContainer
import com.algorithmlx.ecr.api.recipe.CachedRecipe
import com.algorithmlx.ecr.api.utils.count
import com.algorithmlx.ecr.common.init.registry.BlockEntityTypeRegistry
import com.algorithmlx.ecr.common.init.registry.MRUTypeRegistry
import com.algorithmlx.ecr.common.init.registry.RecipeTypeRegistry
import com.algorithmlx.ecr.common.menu.EnvoyerMenu
import net.minecraft.core.BlockPos
import net.minecraft.core.NonNullList
import net.minecraft.network.chat.Component
import net.minecraft.world.ContainerHelper
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ContainerData
import net.minecraft.world.inventory.ContainerLevelAccess
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.CraftingInput
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput
import kotlin.jvm.optionals.getOrNull

class EnvoyerBlockEntity(
    worldPosition: BlockPos,
    blockState: BlockState
) : BaseContainerBlockEntity(BlockEntityTypeRegistry.instance.envoyer, worldPosition, blockState), MRUDevice {
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

    private val recipe = CachedRecipe(RecipeTypeRegistry.instance.envoyer)

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
        ContainerHelper.saveAllItems(output, this.items)
        output.putInt("progress", this.progress)
        output.putInt("max_progress", this.maxProgress)
        this.mruStorage.save(output)
        super.saveAdditional(output)
    }

    override fun loadAdditional(input: ValueInput) {
        ContainerHelper.loadAllItems(input, this.items)
        this.progress = input.getIntOr("progress", 0)
        this.maxProgress = input.getIntOr("max_progress", 0)
        this.mruStorage.load(input)
        super.loadAdditional(input)
    }

    override fun getContainerSize(): Int = 8

    override fun canPlaceItem(slot: Int, itemStack: ItemStack): Boolean = if (slot == 5) false else super.canPlaceItem(slot, itemStack)

    override val mruStorage: IOMRUStorage = MRUStorageContainer(5000, MRUTypeRegistry.instance.espe) { setChanged() }
    override val holderType: MRUDevice.DeviceType = MRUDevice.DeviceType.RECEIVER

    override val locator: MRUDevice.LocatorData = MRUDevice.LocatorData(this, 6)

    companion object {
        fun onTick(level: Level, be: EnvoyerBlockEntity) {
            if (level.isClientSide) return
            be.processReceive(level)
            be.processRecipeIfPresent(level)
        }

        private fun EnvoyerBlockEntity.processRecipeIfPresent(level: Level) {
            if ((0 ..< 5).all { this.getItem(it).isEmpty }) return
            val container = CraftingInput.of(2, 3, mutableListOf<ItemStack>().also { list ->
                (0 ..< 5).forEach { fe -> list.add(getItem(fe)) }
                list.add(ItemStack.EMPTY)
            })

            val recipe = this.recipe.testAndGet(container, level)
            if (recipe == null) {
                this.resetProgress()
                return
            }

            val time = recipe.time
            val mru = recipe.mruPerTick
            val result = recipe.result

            this.maxProgress = time

            if (this.getItem(5).isEmpty) {
                this.processTick(time, mru)

                if (time > this.progress) return

                val inputs = recipe.inputs.getOrNull()
                val ingredientOptionals = inputs?.ingredients()

                (0 ..< 4).forEach { ingredientOptionals?.get(it)?.getOrNull()?.let { l -> this.removeItem(it, l.count) } }

                val catalyst = recipe.catalyst.getOrNull()
                catalyst?.let { this.removeItem(4, it.count) }

                if (this.getItem(5).isEmpty)
                    this.setItem(5, result.create())
                else this.getItem(5).grow(result.count)

                this.resetProgress()
            }
        }

        private fun EnvoyerBlockEntity.processTick(time: Int, mru: Int) {
            val storage = this.mruStorage
            if (this.progress >= time || !storage.canExtract(mru)) return

            storage.extract(mru)
            this.progress++
            this.setChanged()
        }

        private fun EnvoyerBlockEntity.resetProgress() {
            this.progress = 0
            this.maxProgress = 0
            this.setChanged()
        }
    }
}
