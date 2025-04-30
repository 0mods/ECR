package team._0mods.ecr.common.blocks.entity

import net.minecraft.core.BlockPos
import net.minecraft.core.NonNullList
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.world.MenuProvider
import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ContainerData
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import ru.hollowhorizon.hc.common.utils.get
import ru.hollowhorizon.hc.common.capabilities.CapabilityInstance
import ru.hollowhorizon.hc.common.capabilities.HollowCapability
import ru.hollowhorizon.hc.common.capabilities.containers.HollowContainer
import ru.hollowhorizon.hc.common.capabilities.containers.ItemStorage
import ru.hollowhorizon.hc.common.capabilities.containers.container
import ru.hollowhorizon.hc.common.objects.blocks.HollowBlockEntity
import team._0mods.ecr.api.mru.MRUHolder
import team._0mods.ecr.api.mru.MRUStorage
import team._0mods.ecr.api.mru.MRUTypes
import team._0mods.ecr.api.mru.processReceive
import team._0mods.ecr.common.api.ContainerLevelAccess
import team._0mods.ecr.common.init.registry.ECRRegistry
import team._0mods.ecr.common.menu.XLikeMenu
import team._0mods.ecr.common.recipes.XLikeRecipe

abstract class XLikeBlockEntity(
    bet: BlockEntityType<*>,
    pos: BlockPos,
    state: BlockState
): HollowBlockEntity(bet, pos, state), MenuProvider, MRUHolder {
    protected val containerData: ContainerData = object : ContainerData {
        override fun get(index: Int): Int  = when (index) {
            0 -> this@XLikeBlockEntity.progress
            1 -> this@XLikeBlockEntity.maxProgress
            else -> 0
        }

        override fun set(index: Int, value: Int) {
            when (index) {
                0 -> this@XLikeBlockEntity.progress = value
                1 -> this@XLikeBlockEntity.maxProgress = value
            }
        }

        override fun getCount(): Int = 2
    }

    var progress = 0
    var maxProgress = 0

    override val holderType: MRUHolder.MRUHolderType = MRUHolder.MRUHolderType.RECEIVER

    abstract override val mruContainer: MRUStorage
    abstract override val locator: MRUHolder.LocatorData

    override fun saveAdditional(tag: CompoundTag) {
        tag.putInt("Progress", progress)
        tag.putInt("MaxProgress", maxProgress)
        super.saveAdditional(tag)
    }

    override fun load(tag: CompoundTag) {
        progress = tag.getInt("Progress")
        maxProgress = tag.getInt("MaxProgress")
        super.load(tag)
    }

    override fun getDisplayName(): Component = Component.empty()

    fun <T: XLikeRecipe, X: XLikeBlockEntity> processRecipeIfPresent(level: Level, recipeType: RecipeType<T>, be: X, needEmptySlot: Boolean = false) {
        if (level.isClientSide) return
        val container = this[ItemContainer::class].items
        val list = NonNullList.withSize(5, ItemStack.EMPTY)
        (0 ..< 5).forEach {
            if (!container.getItem(it).isEmpty)
                list[it] = container.getItem(it)
        }

        val inv = SimpleContainer(5).apply { (0 ..< this.containerSize).forEach { this.setItem(it, list[it]) } }
        val recipeOptional = level.recipeManager.getRecipeFor(recipeType, inv, level)

        if (recipeOptional.isPresent) {
            val recipe = recipeOptional.get()
            val time = recipe.time
            val mru = recipe.mruPerTick
            val result = recipe.getResultItem(level.registryAccess())

            be.maxProgress = time

            if (!needEmptySlot || container.getItem(5).isEmpty) {
                this.processTick(time, mru)
                if (this.progress >= time) {
                    inv.clearContent()
                    (0 ..< 5).forEach {
                        if (!container.getItem(it).isEmpty)
                            container.removeItem(it, recipe.ingredients[it].items[0].count)
                    }

                    if (container.getItem(5).isEmpty)
                        container.setItem(5, result.copy())
                    else container.getItem(5).grow(result.count)
                    this.resetProgress()
                }
            }
        } else {
            inv.clearContent()
            this.resetProgress()
        }
    }

    private fun XLikeBlockEntity.processTick(time: Int, mru: Int) {
        val storage = this.mruContainer
        if (this.progress >= time) return
        if (!storage.canExtract(mru)) return

        storage.extractMru(mru)
        this.progress++
        this.setChanged()
    }

    private fun XLikeBlockEntity.resetProgress() {
        this.progress = 0
        this.maxProgress = 0
        this.setChanged()
    }

    @HollowCapability(XLikeBlockEntity::class)
    class MRUContainer: CapabilityInstance(), MRUStorage {
        override var mru: Int by syncable(0)
        override val maxMRUStorage: Int = 5000
        override val mruType: MRUTypes = MRUTypes.RADIATION_UNIT
    }

    @HollowCapability(XLikeBlockEntity::class)
    class ItemContainer: CapabilityInstance(), ItemStorage {
        internal var stackSize by syncable(64)
        internal var containerSize by syncable(7)
        override val items by container(SizeableContainer(stackSize))

        inner class SizeableContainer(private val stackSize: Int): HollowContainer(this, containerSize, { slot, _ -> slot == 5 }) {
            override fun getContainerSize(): Int = stackSize
        }
    }

    class Envoyer(pos: BlockPos, state: BlockState): XLikeBlockEntity(ECRRegistry.envoyerEntity, pos, state) {
        init {
            this[ItemContainer::class].stackSize = 1
        }

        override fun createMenu(
            id: Int,
            inv: Inventory,
            player: Player
        ): AbstractContainerMenu? {
            return XLikeMenu.Envoyer(id, inv, this[ItemContainer::class].items, this, ContainerLevelAccess(this.level ?: return null, this.blockPos), containerData)
        }

        companion object {
            @JvmStatic
            fun onTick(level: Level, pos: BlockPos, state: BlockState, be: Envoyer) {
                be.processReceive(level)
                be.processRecipeIfPresent(level, ECRRegistry.envoyerRecipe, be, true)
            }
        }

        override val mruContainer: MRUStorage by lazy { this@Envoyer[MRUContainer::class] }

        override val locator: MRUHolder.LocatorData = MRUHolder.LocatorData(this[ItemContainer::class], 6)
    }

    class MagicTable(pos: BlockPos, state: BlockState): XLikeBlockEntity(ECRRegistry.magicTableEntity, pos, state) {
        init {
            this[ItemContainer::class].containerSize = 8
        }

        override fun createMenu(
            id: Int,
            inv: Inventory,
            player: Player
        ): AbstractContainerMenu? {
            return XLikeMenu.MagicTable(id, inv, this[ItemContainer::class].items, this, ContainerLevelAccess(this.level ?: return null, this.blockPos), containerData)
        }

        companion object {
            @JvmStatic
            fun onTick(level: Level, pos: BlockPos, state: BlockState, be: MagicTable) {
                be.processReceive(level)
                be.processRecipeIfPresent(level, ECRRegistry.magicTableRecipe, be)
            }
        }

        override val mruContainer: MRUStorage by lazy { this@MagicTable[MRUContainer::class] }

        override val locator: MRUHolder.LocatorData = MRUHolder.LocatorData(this[ItemContainer::class], 6)
    }
}
