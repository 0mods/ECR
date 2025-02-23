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
import team._0mods.ecr.api.item.SoulStoneLike
import team._0mods.ecr.api.mru.MRUGenerator
import team._0mods.ecr.api.mru.MRUStorage
import team._0mods.ecr.api.mru.MRUTypes
import team._0mods.ecr.api.utils.SoulStoneUtils.capacity
import team._0mods.ecr.api.utils.SoulStoneUtils.consumeUBMRU
import team._0mods.ecr.api.utils.SoulStoneUtils.isCreative
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
    @JvmField
    var status: MatrixDestructorStatus? = null

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

        if (status == null) tag.remove("Status")
        else tag.putString("Status", status.toString())

        super.saveAdditional(tag)
    }

    override fun load(tag: CompoundTag) {
        itemHandler.deserializeNBT(tag.getCompound("ItemStorage"))
        progress = tag.getInt("InjectionProgress")
        if (tag.contains("Status")) {
            val t = tag.getString("Status")
            if (MatrixDestructorStatus.entries.any { it.name == t })
                status = MatrixDestructorStatus.valueOf(t)
            else {
                status = null
                tag.remove("Status")
            }
        }
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

    enum class MatrixDestructorStatus {
        WAITING, WORKING, WARNING, STOPPED
    }

    fun disable() = this.setStatus(null)

    fun working() = this.setStatus(MatrixDestructorStatus.WORKING)

    fun waiting() = this.setStatus(MatrixDestructorStatus.WAITING)

    fun warning() = this.setStatus(MatrixDestructorStatus.WARNING)

    fun stopped() = this.setStatus(MatrixDestructorStatus.STOPPED)

    private fun setStatus(status: MatrixDestructorStatus?) {
        this.status = status
        this.setChanged()
    }

    companion object {
        @JvmStatic
        fun onTick(level: Level, pos: BlockPos, state: BlockState, be: MatrixDestructorEntity) {
            if (level.isClientSide || be.mruContainer.mru >= be.mruContainer.maxMRUStorage) return

            val stack = be.itemHandler.getStackInSlot(0)
            if (stack.isEmpty) return

            val storage = stack.capacity
            val i = stack.item as? SoulStoneLike ?: return
            val convertCost = i.extractCount
            val receiveCost = i.receiveCount
            val hasEnoughStorage = storage >= receiveCost || stack.isCreative

            switchStatus(be, storage, stack)

            if (!hasEnoughStorage) return

            val convertAmount = if (storage >= convertCost || stack.isCreative) convertCost else 0
            stack.consumeUBMRU(convertAmount)
            be.progress += convertAmount

            if (be.progress >= convertCost) {
                be.progress = 0
                be.mruContainer.receiveMru(receiveCost)
                be.setChanged()
            }
        }

        @JvmStatic
        private fun switchStatus(
            be: MatrixDestructorEntity,
            storage: Int,
            stack: ItemStack
        ) {
            val hasPower = storage > 0 || stack.isCreative

            when {
                be.mruContainer.isEmpty -> be.disable()
                !be.mruContainer.isFilled && !hasPower -> be.stopped()
                be.progress > 0 && hasPower -> be.working()
                be.mruContainer.isFilled && hasPower -> be.warning()
                be.mruContainer.hasMRU && stack.isEmpty -> be.waiting()
            }
        }
    }
}