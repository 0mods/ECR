package team._0mods.ecr.common.blocks.entity

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ContainerLevelAccess
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ForgeCapabilities
import net.minecraftforge.common.util.LazyOptional
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.ItemStackHandler
import team._0mods.ecr.api.mru.MRUReceivable
import team._0mods.ecr.api.mru.processReceive
import team._0mods.ecr.common.capability.MRUContainer
import team._0mods.ecr.common.capability.impl.MRUContainerImpl
import team._0mods.ecr.common.container.EnvoyerContainer
import team._0mods.ecr.common.init.registry.ECRegistry

class EnvoyerBlockEntity(pos: BlockPos, blockState: BlockState) : BlockEntity(ECRegistry.envoyerEntity.get(), pos, blockState), MenuProvider, MRUReceivable {
    private val itemHandler = object : ItemStackHandler(7) {
        override fun onContentsChanged(slot: Int) {
            super.onContentsChanged(slot)
            setChanged()
        }

        override fun getSlotLimit(slot: Int): Int {
            return if (slot != 6) 1 else super.getSlotLimit(slot)
        }
    }

    private val mruStorage = MRUContainerImpl(MRUContainer.MRUType.RADIATION_UNIT, 5000, 0)

    private var itemHandlerLazy = LazyOptional.empty<IItemHandler>()
    private var mruStorageLazy = LazyOptional.empty<MRUContainer>()

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
        mruStorage.deserializeNBT(tag.getCompound("MRU"))
        super.load(tag)
    }

    override fun <T : Any?> getCapability(cap: Capability<T>, side: Direction?): LazyOptional<T> {
        if (cap == ForgeCapabilities.ITEM_HANDLER) return itemHandlerLazy.cast()

        return super.getCapability(cap, side)
    }

    override fun createMenu(i: Int, arg: Inventory, arg2: Player): AbstractContainerMenu? {
        return EnvoyerContainer(i, arg, this.itemHandler, ContainerLevelAccess.create(this.level ?: return null, this.blockPos))
    }

    override fun getDisplayName(): Component = Component.empty()

    companion object {
        @JvmStatic
        fun onTick(level: Level, pos: BlockPos, state: BlockState, be: EnvoyerBlockEntity) {
            be.processReceive(level)
        }
    }

    override val positionCrystal: ItemStack
        get() = this.itemHandler.getStackInSlot(6)

    override val mruContainer: MRUContainer
        get() = this.mruStorage
}