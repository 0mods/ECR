package team._0mods.ecr.common.blocks.entity

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.IntTag
import net.minecraft.network.chat.Component
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ContainerLevelAccess
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ForgeCapabilities
import net.minecraftforge.common.util.LazyOptional
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.ItemStackHandler
import team._0mods.ecr.api.mru.MRUGenerator
import team._0mods.ecr.api.mru.MRUStorage
import team._0mods.ecr.api.mru.MRUTypes
import team._0mods.ecr.common.api.SyncedBlockEntity
import team._0mods.ecr.common.capability.MRUContainer
import team._0mods.ecr.common.container.MatrixDestructorContainer
import team._0mods.ecr.common.init.config.ECCommonConfig
import team._0mods.ecr.common.init.registry.ECCapabilities
import team._0mods.ecr.common.init.registry.ECRegistry
import team._0mods.ecr.common.items.SoulStone

class MatrixDestructorEntity(pos: BlockPos, blockState: BlockState) :
    SyncedBlockEntity(ECRegistry.matrixDestructorEntity.get(), pos, blockState), MenuProvider, MRUGenerator {
    val itemHandler = object : ItemStackHandler(1) {
        override fun onContentsChanged(slot: Int) {
            setChanged()
        }
    }

    val mruContainer = MRUContainer(MRUTypes.RADIATION_UNIT, 10000, 0) {
        if (!level!!.isClientSide) setChanged()
    }

    private var itemHandlerLazy = LazyOptional.empty<IItemHandler>()
    private var mruStorageLazy = LazyOptional.empty<MRUStorage>()

    var progress = 0

    override val currentMRUStorage = mruContainer

    override fun onLoad() {
        super.onLoad()
        itemHandlerLazy = LazyOptional.of(::itemHandler)
        mruStorageLazy = LazyOptional.of(::mruContainer)
    }

    override fun invalidateCaps() {
        super.invalidateCaps()
        itemHandlerLazy.invalidate()
        mruStorageLazy.invalidate()
    }

    override fun saveAdditional(tag: CompoundTag) {
        tag.put("ItemStorage", itemHandler.serializeNBT())
        tag.put("MRU", mruContainer.serializeNBT())
        tag.putInt("InjectionProgress", progress)
        super.saveAdditional(tag)
    }

    override fun load(tag: CompoundTag) {
        itemHandler.deserializeNBT(tag.getCompound("ItemStorage"))
        mruContainer.deserializeNBT(IntTag.valueOf(tag.getInt("MRUStorage")))
        progress = tag.getInt("InjectionProgress")
        super.load(tag)
    }

    override fun <T : Any?> getCapability(cap: Capability<T>, side: Direction?): LazyOptional<T> {
        if (cap == ForgeCapabilities.ITEM_HANDLER) return itemHandlerLazy.cast()

        if (cap == ECCapabilities.MRU_CONTAINER) return mruStorageLazy.cast()

        return super.getCapability(cap, side)
    }

    override fun createMenu(id: Int, inv: Inventory, arg2: Player): AbstractContainerMenu? {
        return MatrixDestructorContainer(
            id,
            inv,
            this.itemHandler,
            this,
            ContainerLevelAccess.create(this.level ?: return null, this.blockPos)
        )
    }

    override fun getDisplayName(): Component = Component.empty()

    companion object {
        @JvmStatic
        fun onTick(level: Level, pos: BlockPos, state: BlockState, be: MatrixDestructorEntity) {
            if (level.isClientSide || be.mruContainer.mruStorage >= be.mruContainer.maxMRUStorage) return

            val convertCost = ECCommonConfig.instance.matrixConsuming
            val receiveCost = ECCommonConfig.instance.matrixResult
            val stack = be.itemHandler.getStackInSlot(0)

            val soulStone = stack.item as? SoulStone ?: return
            val storage = soulStone.getCapacity(stack)

            if (stack.isEmpty || storage < receiveCost) return

            val convertAmount = if (storage >= convertCost) convertCost else 1
            soulStone.remove(stack, convertAmount)
            be.progress += convertAmount

            if (be.progress >= convertCost) {
                be.progress = 0
                be.mruContainer.receiveMru(receiveCost)
                be.setChanged()
            }
        }
    }
}