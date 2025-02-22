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
import net.minecraft.world.level.block.state.BlockState
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ForgeCapabilities
import net.minecraftforge.common.util.LazyOptional
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.ItemStackHandler
import ru.hollowhorizon.hc.client.utils.get
import ru.hollowhorizon.hc.common.capabilities.CapabilityInstance
import ru.hollowhorizon.hc.common.capabilities.HollowCapabilityV2
import ru.hollowhorizon.hc.common.objects.blocks.HollowBlockEntity
import team._0mods.ecr.api.mru.MRUGenerator
import team._0mods.ecr.api.mru.MRUStorage
import team._0mods.ecr.api.mru.MRUTypes
import team._0mods.ecr.api.utils.SoulStoneUtils.capacity
import team._0mods.ecr.api.utils.SoulStoneUtils.consumeUBMRU
import team._0mods.ecr.api.utils.SoulStoneUtils.isCreative
import team._0mods.ecr.common.init.config.ECCommonConfig
import team._0mods.ecr.common.init.registry.ECRegistry
import team._0mods.ecr.common.menu.MatrixDestructorMenu

class MatrixDestructorEntity(pos: BlockPos, blockState: BlockState) :
    HollowBlockEntity(ECRegistry.matrixDestructorEntity.get(), pos, blockState), MenuProvider, MRUGenerator {
    val itemHandler = object : ItemStackHandler(1) {
        override fun onContentsChanged(slot: Int) {
            setChanged()
        }
    }

    private var itemHandlerLazy = LazyOptional.empty<IItemHandler>()

    var progress = 0

    override val mruContainer = this[MRUContainer::class]

    override fun onLoad() {
        super.onLoad()
        itemHandlerLazy = LazyOptional.of(::itemHandler)
    }

    override fun invalidateCaps() {
        super.invalidateCaps()
        itemHandlerLazy.invalidate()
    }

    override fun saveAdditional(tag: CompoundTag) {
        tag.put("ItemStorage", itemHandler.serializeNBT())
        tag.putInt("InjectionProgress", progress)
        super.saveAdditional(tag)
    }

    override fun load(tag: CompoundTag) {
        itemHandler.deserializeNBT(tag.getCompound("ItemStorage"))
        progress = tag.getInt("InjectionProgress")
        super.load(tag)
    }

    override fun <T : Any?> getCapability(cap: Capability<T>, side: Direction?): LazyOptional<T> {
        if (cap == ForgeCapabilities.ITEM_HANDLER) return itemHandlerLazy.cast()

        return super.getCapability(cap, side)
    }

    override fun createMenu(id: Int, inv: Inventory, arg2: Player): AbstractContainerMenu? {
        return MatrixDestructorMenu(
            id,
            inv,
            this.itemHandler,
            this,
            ContainerLevelAccess.create(this.level ?: return null, this.blockPos)
        )
    }

    override fun getDisplayName(): Component = Component.empty()

    @HollowCapabilityV2(MatrixDestructorEntity::class)
    class MRUContainer: CapabilityInstance(), MRUStorage {
        override var mru: Int by syncable(0)
        override val maxMRUStorage: Int = 10000

        override val mruType: MRUTypes = MRUTypes.ESPE
    }

    companion object {
        @JvmStatic
        fun onTick(level: Level, pos: BlockPos, state: BlockState, be: MatrixDestructorEntity) {
            if (level.isClientSide || be.mruContainer.mru >= be.mruContainer.maxMRUStorage) return

            val convertCost = ECCommonConfig.instance.soulStoneExtractCount
            val receiveCost = ECCommonConfig.instance.soulStoneReceiveCount
            val stack = be.itemHandler.getStackInSlot(0)
            val storage = stack.capacity

            if (stack.isEmpty || storage < receiveCost) return

            val convertAmount = if (storage >= convertCost || stack.isCreative) convertCost else 0
            stack.consumeUBMRU(convertAmount)
            be.progress += convertAmount

            if (be.progress >= convertCost) {
                be.progress = 0
                be.mruContainer.receiveMru(receiveCost)
                be.setChanged()
            }
        }
    }
}