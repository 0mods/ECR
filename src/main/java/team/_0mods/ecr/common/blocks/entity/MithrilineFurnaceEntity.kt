package team._0mods.ecr.common.blocks.entity

import net.minecraft.core.BlockPos
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
import net.minecraftforge.items.ItemStackHandler
import team._0mods.ecr.ModId
import team._0mods.ecr.common.capability.MRUContainer
import team._0mods.ecr.common.capability.impl.MRUContainerImpl
import team._0mods.ecr.common.container.MithrilineFurnaceContainer
import team._0mods.ecr.common.init.registry.ECCapabilities
import team._0mods.ecr.common.init.registry.ECMultiblocks
import team._0mods.ecr.common.init.registry.ECRegistry

class MithrilineFurnaceEntity(pos: BlockPos, blockState: BlockState) :
    BlockEntity(ECRegistry.mithrilineFurnace.second, pos, blockState), MenuProvider {
    companion object {
        @JvmStatic
        fun onTick(level: Level, pos: BlockPos, state: BlockState, be: MithrilineFurnaceEntity) {
            be.successfulStructure = ECMultiblocks.mithrilineFurnace.isComplete(level, pos)
            val complete = be.successfulStructure

            if (complete) {
            }
        }
    }

    private val itemHandler = createStackHandler()
    private val mruStorage = createMruStorage()

    private val itemHandlerLazy = LazyOptional.of(::itemHandler)
    private val mruStorageLazy = LazyOptional.of(::mruStorage)

    var successfulStructure = false

    override fun saveAdditional(tag: CompoundTag) {
        super.saveAdditional(tag)
        tag.put("ItemStorage", itemHandler.serializeNBT())
        tag.put("MRUSUStorage", mruStorage.serializeNBT())
        tag.putBoolean("FullStructure", successfulStructure)
    }

    override fun load(tag: CompoundTag) {
        super.load(tag)
        itemHandler.deserializeNBT(tag.getCompound("ItemStorage"))
        mruStorage.deserializeNBT(tag.getCompound("MRUSUStorage"))
        successfulStructure = tag.getBoolean("Working")
    }

    override fun createMenu(id: Int, inv: Inventory, player: Player): AbstractContainerMenu? {
        return MithrilineFurnaceContainer(id, inv, this.blockPos, itemHandler, ContainerLevelAccess.create(this.level ?: return null, this.blockPos))
    }

    override fun getDisplayName(): Component = Component.translatable("container.$ModId.mithriline_furnace")

    override fun <T : Any?> getCapability(cap: Capability<T>): LazyOptional<T> {
        if (cap == ForgeCapabilities.ITEM_HANDLER)
            return itemHandlerLazy.cast()

        if (cap == ECCapabilities.MRU_CONTAINER) {
            return mruStorageLazy.cast()
        }

        return super.getCapability(cap)
    }

    private fun createStackHandler() = object : ItemStackHandler(2) {
        override fun onContentsChanged(slot: Int) {
            setChanged()
        }
    }

    private fun createMruStorage() = MRUContainerImpl(MRUContainer.MRUType.MRUSU, 10000, 0, 0, 0)
}
