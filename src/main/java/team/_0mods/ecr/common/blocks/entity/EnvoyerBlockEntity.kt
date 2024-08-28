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
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ForgeCapabilities
import net.minecraftforge.common.util.LazyOptional
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.ItemStackHandler
import team._0mods.ecr.common.container.EnvoyerContainer
import team._0mods.ecr.common.init.registry.ECRegistry

class EnvoyerBlockEntity(pos: BlockPos, blockState: BlockState) : BlockEntity(ECRegistry.envoyer.second, pos, blockState), MenuProvider {
    private val itemHandler = object : ItemStackHandler(7) {
        override fun onContentsChanged(slot: Int) {
            super.onContentsChanged(slot)
            setChanged()
        }

        override fun getSlotLimit(slot: Int): Int {
            return if (slot != 6) 1 else super.getSlotLimit(slot)
        }
    }

    private var itemHandlerLazy = LazyOptional.empty<IItemHandler>()

    var progress = 0
    var maxProgress = 0

    override fun onLoad() {
        itemHandlerLazy = LazyOptional.of(::itemHandler)
        super.onLoad()
    }

    override fun invalidateCaps() {
        super.invalidateCaps()
        itemHandlerLazy.invalidate()
    }

    override fun saveAdditional(tag: CompoundTag) {
        tag.putInt("Progress", progress)
        tag.putInt("MaxProgress", maxProgress)
        tag.put("Items", itemHandler.serializeNBT())
        super.saveAdditional(tag)
    }

    override fun load(tag: CompoundTag) {
        progress = tag.getInt("Progress")
        maxProgress = tag.getInt("MaxProgress")
        itemHandler.deserializeNBT(tag.getCompound("Items"))
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
        fun onTick(level: Level, pos: BlockPos, state: BlockState, be: EnvoyerBlockEntity) {}
    }
}