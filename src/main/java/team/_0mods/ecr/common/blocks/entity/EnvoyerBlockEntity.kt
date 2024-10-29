package team._0mods.ecr.common.blocks.entity

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.NonNullList
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.IntTag
import net.minecraft.network.chat.Component
import net.minecraft.world.MenuProvider
import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ContainerLevelAccess
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ForgeCapabilities
import net.minecraftforge.common.util.LazyOptional
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.ItemStackHandler
import team._0mods.ecr.api.mru.MRUReceivable
import team._0mods.ecr.api.mru.MRUStorage
import team._0mods.ecr.api.mru.MRUTypes
import team._0mods.ecr.api.mru.processReceive
import team._0mods.ecr.common.api.SyncedBlockEntity
import team._0mods.ecr.common.capability.MRUStorageImpl
import team._0mods.ecr.common.container.EnvoyerContainer
import team._0mods.ecr.common.init.registry.ECCapabilities
import team._0mods.ecr.common.init.registry.ECRegistry

class EnvoyerBlockEntity(pos: BlockPos, blockState: BlockState) : SyncedBlockEntity(
    ECRegistry.envoyerEntity.get(),
    pos,
    blockState
), MenuProvider, MRUReceivable {
    private val itemHandler = object : ItemStackHandler(7) {
        override fun onContentsChanged(slot: Int) {
            super.onContentsChanged(slot)
            setChanged()
        }

        override fun getSlotLimit(slot: Int): Int {
            return if (slot != 6) 1 else super.getSlotLimit(slot)
        }
    }

    private val mruStorage = MRUStorageImpl(MRUTypes.RADIATION_UNIT, 5000, 0) { setChanged() }

    private var itemHandlerLazy = LazyOptional.empty<IItemHandler>()
    private var mruStorageLazy = LazyOptional.empty<MRUStorage>()

    var progress = 0
    var maxProgress = 0

    override fun onLoad() {
        itemHandlerLazy = LazyOptional.of(::itemHandler)
        mruStorageLazy = LazyOptional.of(::mruStorage)
        super.onLoad()
    }

    override fun invalidateCaps() {
        super.invalidateCaps()
        itemHandlerLazy.invalidate()
        mruStorageLazy.invalidate()
    }

    override fun saveAdditional(tag: CompoundTag) {
        tag.putInt("Progress", progress)
        tag.putInt("MaxProgress", maxProgress)
        tag.put("Items", itemHandler.serializeNBT())
        tag.put("MRU", mruStorage.serializeNBT())
        super.saveAdditional(tag)
    }

    override fun load(tag: CompoundTag) {
        progress = tag.getInt("Progress")
        maxProgress = tag.getInt("MaxProgress")
        itemHandler.deserializeNBT(tag.getCompound("Items"))
        mruStorage.deserializeNBT(IntTag.valueOf(tag.getInt("MRU")))
        super.load(tag)
    }

    override fun <T : Any?> getCapability(cap: Capability<T>, side: Direction?): LazyOptional<T> {
        if (cap == ForgeCapabilities.ITEM_HANDLER) return itemHandlerLazy.cast()

        if (cap == ECCapabilities.MRU_CONTAINER) return mruStorageLazy.cast()

        return super.getCapability(cap, side)
    }

    override fun createMenu(i: Int, arg: Inventory, arg2: Player): AbstractContainerMenu? {
        return EnvoyerContainer(i, arg, this.itemHandler, this, ContainerLevelAccess.create(this.level ?: return null, this.blockPos))
    }

    override fun getDisplayName(): Component = Component.empty()

    companion object {
        @JvmStatic
        fun onTick(level: Level, pos: BlockPos, state: BlockState, be: EnvoyerBlockEntity) {
            if (!level.isClientSide) {
                be.processReceive(level)
                be.processRecipeIfPresent(level)
            }
        }

        @JvmStatic
        private fun EnvoyerBlockEntity.processRecipeIfPresent(level: Level) {
            val list = NonNullList.withSize(5, ItemStack.EMPTY)
            (0 ..< 5).forEach {
                if (!this.itemHandler.getStackInSlot(it).isEmpty)
                    list[it] = this.itemHandler.getStackInSlot(it)
            }

            val inv = SimpleContainer(5).apply { (0 ..< this.containerSize).forEach { this.setItem(it, list[it]) } }
            val recipeOptional = level.recipeManager.getRecipeFor(ECRegistry.envoyerRecipe.get(), inv, level)

            if (recipeOptional.isPresent) {
                val recipe = recipeOptional.get()
                val time = recipe.time
                val mru = recipe.mruPerTick
                val result = recipe.getResultItem(level.registryAccess())

                if (this.itemHandler.getStackInSlot(5).isEmpty) {
                    this.processTick(time, mru)
                    if (this.progress >= time) {
                        inv.clearContent()
                        (0 ..< 5).forEach { this.itemHandler.extractItem(it, 1, false) }
                        this.itemHandler.insertItem(5, result.copy(), false)
                        this.resetProgress()
                    }
                }
            } else {
                inv.clearContent()
                this.resetProgress()
            }
        }

        private fun EnvoyerBlockEntity.processTick(time: Int, mru: Int) {
            val storage = this.mruStorage
            if (this.progress >= time) return
            if (!storage.canExtract(mru)) return

            storage.extractMru(mru)
            this.progress++
            this.setChanged()
        }

        @JvmStatic
        private fun EnvoyerBlockEntity.resetProgress() {
            this.progress = 0
            this.maxProgress = 0
            this.setChanged()
        }
    }

    override val positionCrystal: ItemStack
        get() = this.itemHandler.getStackInSlot(6)

    override val mruContainer: MRUStorage
        get() = this.mruStorage
}