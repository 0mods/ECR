package team._0mods.ecr.common.blocks.entity

import net.minecraft.core.BlockPos
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
import ru.hollowhorizon.hc.common.utils.get
import ru.hollowhorizon.hc.common.capabilities.CapabilityInstance
import ru.hollowhorizon.hc.common.capabilities.HollowCapability
import ru.hollowhorizon.hc.common.capabilities.containers.HollowContainer
import ru.hollowhorizon.hc.common.capabilities.containers.ItemStorage
import ru.hollowhorizon.hc.common.capabilities.containers.container
import ru.hollowhorizon.hc.common.objects.blocks.HollowBlockEntity
import team._0mods.ecr.api.item.SoulStoneLike
import team._0mods.ecr.api.mru.MRUHolder
import team._0mods.ecr.api.mru.MRUStorage
import team._0mods.ecr.api.mru.MRUTypes
import team._0mods.ecr.api.utils.SoulStoneUtils.capacity
import team._0mods.ecr.api.utils.SoulStoneUtils.consumeUBMRU
import team._0mods.ecr.api.utils.SoulStoneUtils.isCreative
import team._0mods.ecr.common.init.registry.ECRRegistry
import team._0mods.ecr.common.menu.MatrixDestructorMenu

class MatrixDestructorEntity(pos: BlockPos, blockState: BlockState) :
    HollowBlockEntity(ECRRegistry.matrixDestructorEntity, pos, blockState), MenuProvider, MRUHolder {
    @JvmField var status: MatrixDestructorStatus? = null
    var progress = 0

    override val mruContainer = this[MRUContainer::class]
    override val holderType: MRUHolder.MRUHolderType = MRUHolder.MRUHolderType.TRANSLATOR

    override fun saveAdditional(tag: CompoundTag) {
        tag.putInt("InjectionProgress", progress)

        if (status == null) tag.remove("Status")
        else tag.putString("Status", status.toString())

        super.saveAdditional(tag)
    }

    override fun load(tag: CompoundTag) {
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

    override fun createMenu(id: Int, inv: Inventory, arg2: Player): AbstractContainerMenu? {
        return MatrixDestructorMenu(
            id,
            inv,
            this[ItemContainer::class].items,
            this,
            ContainerLevelAccess.create(this.level ?: return null, this.blockPos)
        )
    }

    override fun getDisplayName(): Component = Component.empty()

    @HollowCapability(MatrixDestructorEntity::class)
    class MRUContainer: CapabilityInstance(), MRUStorage {
        override var mru: Int by syncable(0)
        override val maxMRUStorage: Int = 10000

        override val mruType: MRUTypes = MRUTypes.RADIATION_UNIT
    }

    @HollowCapability(MatrixDestructorEntity::class)
    class ItemContainer: CapabilityInstance(), ItemStorage {
        override val items: HollowContainer by container(1)
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

            val container = be[ItemContainer::class]
            container.synchronize()

            val stack = container.items.getItem(0)
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