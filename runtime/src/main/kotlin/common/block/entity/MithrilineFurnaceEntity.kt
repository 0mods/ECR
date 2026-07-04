package com.algorithmlx.ecr.common.block.entity

import com.algorithmlx.ecr.api.block.entity.syncForNearby
import com.algorithmlx.ecr.api.mru.MRUHolder
import com.algorithmlx.ecr.api.recipe.CachedRecipe
import com.algorithmlx.ecr.api.utils.StackHelper
import com.algorithmlx.ecr.common.components.MRUStorageContainer
import com.algorithmlx.ecr.common.init.BlockEntityTypeRegistry
import com.algorithmlx.ecr.common.init.RecipeTypeRegistry
import com.algorithmlx.ecr.common.init.ResourceKeys
import com.algorithmlx.ecr.common.menu.MithrilineFurnaceMenu
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.HolderLookup
import net.minecraft.core.NonNullList
import net.minecraft.core.registries.Registries
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.world.ContainerHelper
import net.minecraft.world.SimpleContainer
import net.minecraft.world.WorldlyContainer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ContainerData
import net.minecraft.world.inventory.ContainerLevelAccess
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.CraftingInput
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput
import kotlin.math.floor

class MithrilineFurnaceEntity(
    worldPosition: BlockPos,
    blockState: BlockState
): BaseContainerBlockEntity(BlockEntityTypeRegistry.instance.mithrilineFurnaceEntity, worldPosition, blockState), MRUHolder, WorldlyContainer {
    @all:JvmName("items")
    private var items = NonNullList.withSize(2, ItemStack.EMPTY)
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

    val recipe = CachedRecipe(RecipeTypeRegistry.instance.mithrilineRecipeType)

    var structureIsValid = false
    var receivingTicks = 0
    var craftProgress = 0
    var maxCraftProgress = 0
    var slownessGeneration = false
    // Client Only
    var coreRotationPrevious = 0f
    var coreRotationAngle = 0f

    override fun saveAdditional(output: ValueOutput) {
        ContainerHelper.saveAllItems(output, this.items)
        output.putBoolean("structure_valid", structureIsValid)
        output.putBoolean("slow_generation", this.slownessGeneration)
        output.putInt("progress", this.craftProgress)
        output.putInt("max_progress", this.maxCraftProgress)
        mruStorage.save(output)
        super.saveAdditional(output)
    }

    override fun loadAdditional(input: ValueInput) {
        ContainerHelper.loadAllItems(input, this.items)
        structureIsValid = input.getBooleanOr("structure_valid", false)
        slownessGeneration = input.getBooleanOr("slow_generation", false)
        craftProgress = input.getIntOr("progress", 0)
        maxCraftProgress = input.getIntOr("max_progress", 0)
        mruStorage.load(input)
        super.loadAdditional(input)
    }

    override fun getDefaultName(): Component = Component.empty()

    override fun getItems(): NonNullList<ItemStack> = this.items

    override fun setItems(items: NonNullList<ItemStack>) {
        this.items = items
    }

    override fun createMenu(
        containerId: Int,
        inventory: Inventory
    ): AbstractContainerMenu = MithrilineFurnaceMenu(containerId, inventory, this, this, ContainerLevelAccess.create(this.level!!, this.blockPos), containerData)

    override fun getContainerSize(): Int = this.items.size

    override val mruStorage: MRUStorageContainer = MRUStorageContainer(10000, TODO("Not yet implemented"))
    override val holderType: MRUHolder.MRUHolderType = MRUHolder.MRUHolderType.RECEIVER

    override fun getSlotsForFace(direction: Direction): IntArray = intArrayOf(0, 1)

    override fun canPlaceItemThroughFace(
        slot: Int,
        itemStack: ItemStack,
        direction: Direction?
    ): Boolean = this.canPlaceItem(slot, itemStack)

    override fun canTakeItemThroughFace(
        slot: Int,
        itemStack: ItemStack,
        direction: Direction
    ): Boolean = slot == 0

    override fun setChanged() {
        super.setChanged()
        this.syncForNearby()
    }

    override fun getUpdatePacket(): Packet<ClientGamePacketListener> = ClientboundBlockEntityDataPacket.create(this)

    override fun getUpdateTag(registries: HolderLookup.Provider): CompoundTag = this.saveWithFullMetadata(registries)

    companion object {
        private fun MithrilineFurnaceEntity.generateESPE(level: Level, pos: BlockPos) {}

        @JvmStatic
        private fun MithrilineFurnaceEntity.processRecipeIfPresent(level: Level) {
            val input = this.getItem(0)
            if (input.isEmpty) {
                this.resetProgress()
                return
            }

            val craftingInput = CraftingInput.of(input.count, 1, listOf(input))
            val recipe = this.recipe.testAndGet(craftingInput, level)

            if (recipe != null) {
                val espe = recipe.espe
                val ingredientCount = recipe.ingredientCount
                val result = recipe.assemble(craftingInput)

                this.slownessGeneration = true
                this.maxCraftProgress = recipe.espe

                if (StackHelper.canCombine(result.copy(), this.getItem(1), input.count, ingredientCount)) {
                    this.processTick(espe)
                    if (this.craftProgress >= espe) {
                        this.removeItem(0, ingredientCount)
                        if (this.getItem(1).isEmpty)
                            this.setItem(1, result.copy())
                        else this.getItem(1).grow(result.count)
                        this.resetProgress()
                    }
                }
            } else resetProgress()
        }

        @JvmStatic
        private fun MithrilineFurnaceEntity.processTick(mru: Int) {
            val storage = this.mruStorage
            val extractionStep = (1..1000).reversed().firstOrNull { this.canExtract(mru, it) } ?: 0
            storage.extract(extractionStep)
            this.setChanged()
        }

        @JvmStatic
        private fun MithrilineFurnaceEntity.canExtract(mru: Int, max: Int): Boolean {
            val storage = this.mruStorage
            return storage.mru - max >= 0 && mru >= (max + this.craftProgress)
        }

        @JvmStatic
        private fun MithrilineFurnaceEntity.resetProgress() {
            if (this.slownessGeneration) this.slownessGeneration = false
            this.craftProgress = 0
            this.maxCraftProgress = 0
            this.setChanged()
        }

        // Client Only
        @JvmStatic
        private fun MithrilineFurnaceEntity.processRotation() {
            this.coreRotationPrevious = this.coreRotationAngle

            if (this.structureIsValid)
                this.coreRotationAngle += 45f * (1f / 20)
            else if (this.coreRotationAngle % 90 != 0f) {
                this.coreRotationAngle += 45 * (1f / 20) / 2
                if (this.coreRotationAngle % 90 == 0f)
                    this.coreRotationAngle = 90f * floor(this.coreRotationAngle / 90)
            }
        }
    }
}
