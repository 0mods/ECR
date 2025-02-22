package team._0mods.ecr.common.blocks.entity

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
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
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ForgeCapabilities
import net.minecraftforge.common.util.LazyOptional
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.ItemStackHandler
import ru.hollowhorizon.hc.client.utils.get
import ru.hollowhorizon.hc.common.capabilities.CapabilityInstance
import ru.hollowhorizon.hc.common.capabilities.HollowCapabilityV2
import ru.hollowhorizon.hc.common.objects.blocks.HollowBlockEntity
import team._0mods.ecr.api.mru.MRUReceivable
import team._0mods.ecr.api.mru.MRUStorage
import team._0mods.ecr.api.mru.MRUTypes
import team._0mods.ecr.api.mru.processReceive
import team._0mods.ecr.common.api.ContainerLevelAccess
import team._0mods.ecr.common.init.registry.ECRegistry
import team._0mods.ecr.common.menu.XLikeMenu
import team._0mods.ecr.common.recipes.XLikeRecipe

abstract class XLikeBlockEntity(
    bet: BlockEntityType<*>,
    pos: BlockPos,
    state: BlockState
): HollowBlockEntity(bet, pos, state), MenuProvider, MRUReceivable {
    protected var slotLimit: (Int) -> Int = { 64 }

    protected val itemHandler = object : ItemStackHandler(7) {
        override fun onContentsChanged(slot: Int) {
            super.onContentsChanged(slot)
            setChanged()
        }

        override fun getSlotLimit(slot: Int): Int {
            return slotLimit(slot)
        }
    }

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

    override fun getDisplayName(): Component = Component.empty()

    abstract override val locator: ItemStack

    fun <T: XLikeRecipe, X: XLikeBlockEntity> processRecipeIfPresent(level: Level, recipeType: RecipeType<T>, be: X, needEmptySlot: Boolean = false) {
        if (level.isClientSide) return
        val list = NonNullList.withSize(5, ItemStack.EMPTY)
        (0 ..< 5).forEach {
            if (!this.itemHandler.getStackInSlot(it).isEmpty)
                list[it] = this.itemHandler.getStackInSlot(it)
        }

        val inv = SimpleContainer(5).apply { (0 ..< this.containerSize).forEach { this.setItem(it, list[it]) } }
        val recipeOptional = level.recipeManager.getRecipeFor(recipeType, inv, level)

        if (recipeOptional.isPresent) {
            val recipe = recipeOptional.get()
            val time = recipe.time
            val mru = recipe.mruPerTick
            val result = recipe.getResultItem(level.registryAccess())

            be.maxProgress = time

            if (!needEmptySlot || this.itemHandler.getStackInSlot(5).isEmpty) {
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

    @HollowCapabilityV2(XLikeBlockEntity::class)
    class MRUContainer: CapabilityInstance(), MRUStorage {
        override var mru: Int by syncable(0)
        override val maxMRUStorage: Int = 5000
        override val mruType: MRUTypes = MRUTypes.RADIATION_UNIT
    }

    class Envoyer(pos: BlockPos, state: BlockState): XLikeBlockEntity(ECRegistry.envoyerEntity.get(), pos, state) {
        init {
            slotLimit = { if (it != 5) 1 else 64 }
        }

        override fun createMenu(
            id: Int,
            inv: Inventory,
            player: Player
        ): AbstractContainerMenu? {
            return XLikeMenu.Envoyer(id, inv, this.itemHandler, this, ContainerLevelAccess(this.level ?: return null, this.blockPos), containerData)
        }

        companion object {
            @JvmStatic
            fun onTick(level: Level, pos: BlockPos, state: BlockState, be: Envoyer) {
                be.processReceive(level)
                be.processRecipeIfPresent(level, ECRegistry.envoyerRecipe.get(), be, true)
            }
        }

        override val locator: ItemStack = this.itemHandler.getStackInSlot(6)

        override val mruContainer: MRUStorage = this[MRUContainer::class]
    }

    class MagicTable(pos: BlockPos, state: BlockState): XLikeBlockEntity(ECRegistry.magicTableEntity.get(), pos, state) {
        override fun createMenu(
            id: Int,
            inv: Inventory,
            player: Player
        ): AbstractContainerMenu? {
            return XLikeMenu.MagicTable(id, inv, this.itemHandler, this, ContainerLevelAccess(this.level ?: return null, this.blockPos), containerData)
        }

        companion object {
            @JvmStatic
            fun onTick(level: Level, pos: BlockPos, state: BlockState, be: MagicTable) {
                be.processReceive(level)
                be.processRecipeIfPresent(level, ECRegistry.magicTableRecipe.get(), be)
            }
        }

        override val locator: ItemStack = this.itemHandler.getStackInSlot(6)

        override val mruContainer: MRUStorage = this[MRUContainer::class]
    }
}