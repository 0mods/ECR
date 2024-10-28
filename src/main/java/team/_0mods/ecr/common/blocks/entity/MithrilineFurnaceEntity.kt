package team._0mods.ecr.common.blocks.entity

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.IntTag
import net.minecraft.network.chat.Component
import net.minecraft.world.MenuProvider
import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ContainerData
import net.minecraft.world.inventory.ContainerLevelAccess
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ForgeCapabilities
import net.minecraftforge.common.util.LazyOptional
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.ItemStackHandler
import team._0mods.ecr.api.ModId
import team._0mods.ecr.api.block.StructuralPosition
import team._0mods.ecr.api.block.inventory.WrappedInventory
import team._0mods.ecr.api.mru.MRUReceivable
import team._0mods.ecr.api.mru.MRUStorage
import team._0mods.ecr.api.mru.MRUTypes
import team._0mods.ecr.api.utils.StackHelper
import team._0mods.ecr.common.api.SyncedBlockEntity
import team._0mods.ecr.common.capability.MRUStorageImpl
import team._0mods.ecr.common.container.MithrilineFurnaceContainer
import team._0mods.ecr.common.init.config.ECCommonConfig
import team._0mods.ecr.common.init.registry.ECCapabilities
import team._0mods.ecr.common.init.registry.ECRMultiblocks
import team._0mods.ecr.common.init.registry.ECRegistry
import team._0mods.ecr.common.particle.ECParticleOptions
import java.awt.Color
import kotlin.math.floor

class MithrilineFurnaceEntity(pos: BlockPos, blockState: BlockState) :
    SyncedBlockEntity(ECRegistry.mithrilineFurnaceEntity.get(), pos, blockState), MenuProvider, MRUReceivable {
    private val itemHandler = object : ItemStackHandler(2) {
        override fun onContentsChanged(slot: Int) {
            setChanged()
        }
    }

    override val mruContainer = MRUStorageImpl(MRUTypes.ESPE, 10000, 0) { setChanged() }

    private val containerData: ContainerData = object : ContainerData {
        override fun get(index: Int): Int = when (index) {
            0 -> this@MithrilineFurnaceEntity.progress
            1 -> this@MithrilineFurnaceEntity.maxProgress
            else -> 0
        }

        override fun set(index: Int, value: Int) {
            when (index) {
                0 -> this@MithrilineFurnaceEntity.progress = value
                1 -> this@MithrilineFurnaceEntity.maxProgress = value
            }
        }

        override fun getCount(): Int = 2
    }

    private var itemHandlerLazy = LazyOptional.empty<IItemHandler>()
    private var mruStorageLazy = LazyOptional.empty<MRUStorage>()
    private var wrappedHandlerLazy = LazyOptional.empty<WrappedInventory>()

    var successfulStructure = false
    var tickCount = 0
    var progress = 0
    var maxProgress = 0
    var decreaseGeneration = true

    // Calculates only on a client
    @OnlyIn(Dist.CLIENT)
    var previousRot = 0f
    @OnlyIn(Dist.CLIENT)
    var rotAngle = 0f
    // end

    override fun onLoad() {
        super.onLoad()
        itemHandlerLazy = LazyOptional.of(::itemHandler)
        mruStorageLazy = LazyOptional.of(::mruContainer)
        wrappedHandlerLazy = LazyOptional.of {
            WrappedInventory(
                itemHandler,
                { it == 1 && successfulStructure }) { i, s ->
                i == 0 && itemHandler.isItemValid(
                    i,
                    s
                ) && successfulStructure
            }
        }
    }

    override fun invalidateCaps() {
        super.invalidateCaps()
        itemHandlerLazy.invalidate()
        mruStorageLazy.invalidate()
        wrappedHandlerLazy.invalidate()
    }

    override fun saveAdditional(tag: CompoundTag) {
        tag.put("ItemStorage", itemHandler.serializeNBT())
        tag.put("ESPEStorage", mruContainer.serializeNBT())
        tag.putBoolean("FullStructure", successfulStructure)
        tag.putBoolean("DecreaseGeneration", decreaseGeneration)
        tag.putInt("Progress", progress)
        tag.putInt("MaxProgress", maxProgress)
        super.saveAdditional(tag)
    }

    override fun load(tag: CompoundTag) {
        itemHandler.deserializeNBT(tag.getCompound("ItemStorage"))
        mruContainer.deserializeNBT(IntTag.valueOf(tag.getInt("ESPEStorage")))
        successfulStructure = tag.getBoolean("FullStructure")
        decreaseGeneration = tag.getBoolean("DecreaseGeneration")
        progress = tag.getInt("Progress")
        maxProgress = tag.getInt("MaxProgress")
        super.load(tag)
    }

    override fun createMenu(id: Int, inv: Inventory, player: Player): AbstractContainerMenu? {
        return MithrilineFurnaceContainer(
            id,
            inv,
            itemHandler,
            this,
            ContainerLevelAccess.create(this.level ?: return null, this.blockPos),
            this.containerData
        )
    }

    override fun getDisplayName(): Component = Component.translatable("container.$ModId.mithriline_furnace")

    override fun <T : Any?> getCapability(cap: Capability<T>, side: Direction?): LazyOptional<T> {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            if (side == null) return itemHandlerLazy.cast()
            return wrappedHandlerLazy.cast()
        }

        if (cap == ECCapabilities.MRU_CONTAINER) return mruStorageLazy.cast()

        return super.getCapability(cap, side)
    }

    fun getActiveCollectors(level: Level, pos: BlockPos): Int {
        val collectors =
            CRYSTAL_POSITION?.get(pos)?.filter { level.getBlockState(it).block == ECRegistry.mithrilineCrystal.get() }
        if (collectors.isNullOrEmpty()) return 0
        return collectors.size
    }

    companion object {
        @JvmField
        val CRYSTAL_POSITION = makePositions()

        @JvmStatic
        private fun makePositions(): StructuralPosition? {
            val builder = StructuralPosition.builder
            val positions = ECCommonConfig.instance.mithrilineFurnaceConfig.crystalPositions

            if (positions.isEmpty()) return null

            positions.forEach {
                builder.pos(it.x, it.y, it.z)
            }

            return builder.build
        }

        @JvmStatic
        fun onTick(level: Level, pos: BlockPos, state: BlockState, be: MithrilineFurnaceEntity) {
            be.successfulStructure = ECRMultiblocks.mithrilineFurnace.get().isValid(level, pos)

            if (level.isClientSide) {
                processRot(be)
                return
            }

            if (be.successfulStructure) {
                generateESPE(level, pos, be)
                processRecipeIfPresent(be, level)
                level.addParticle(
                    ECParticleOptions(Color.GREEN, 0.5f, 10, 0.1f, true, false),
                    pos.x + 0.5, pos.y + 0.5, pos.z + 0.5, 1.0, 1.0, 1.0
                )
            } else {
                resetProgress(be)
            }
        }

        @JvmStatic
        private fun generateESPE(level: Level, pos: BlockPos, be: MithrilineFurnaceEntity) {
            val collectors = be.getActiveCollectors(level, pos)

            if (collectors > 0 && (be.tickCount++ % (160 / collectors) == 0)) {
                var collect = collectors * 4 - 3 + 1
                if (!be.decreaseGeneration) collect /= 4
                be.mruContainer.receiveMru(collect)
            } else if (CRYSTAL_POSITION == null && (be.tickCount++ % 160 == 0)) {
                be.mruContainer.receiveMru(10)
            }
        }

        @JvmStatic
        private fun processRecipeIfPresent(be: MithrilineFurnaceEntity, level: Level) {
            val inputStack = be.itemHandler.getStackInSlot(0)
            if (inputStack.isEmpty) {
                resetProgress(be)
                return
            }

            val inv = SimpleContainer(1).apply { this.setItem(0, inputStack) }
            val recipe = level.recipeManager.getRecipeFor(ECRegistry.mithrilineFurnaceRecipe.get(), inv, level)

            if (recipe.isPresent) {
                val mfr = recipe.get()
                val result = mfr.result
                val ingrCount = mfr.ingredients[0].items[0].count

                be.decreaseGeneration = false
                be.maxProgress = mfr.espe

                if (canCombine(result.copy(), be.itemHandler.getStackInSlot(1), inputStack.count, ingrCount)) {
                    processTick(be, mfr.espe)
                    if (be.progress >= mfr.espe) {
                        inv.clearContent()
                        be.itemHandler.extractItem(0, ingrCount, false)
                        be.itemHandler.insertItem(1, result.copy(), false)
                        resetProgress(be)
                    }
                }
            } else {
                inv.clearContent()
                resetProgress(be)
            }
        }

        @JvmStatic
        private fun processTick(be: MithrilineFurnaceEntity, neededESPE: Int) {
            val storage = be.mruContainer
            val extractionStep = listOf(1000, 100, 10, 1).firstOrNull { be.checkExtraction(neededESPE, it) } ?: 1
            storage.extractMru(extractionStep)
            be.progress += extractionStep
        }

        private fun MithrilineFurnaceEntity.checkExtraction(neededESPE: Int, max: Int): Boolean {
            val storage = this.mruContainer
            return storage.canExtract(max) && (neededESPE >= (max + this.progress))
        }

        @JvmStatic
        private fun canCombine(result: ItemStack, hand: ItemStack, count: Int, ingredientCount: Int): Boolean =
            StackHelper.canCombineStacks(result, hand) && count >= ingredientCount

        @JvmStatic
        private fun resetProgress(be: MithrilineFurnaceEntity) {
            if (!be.decreaseGeneration) be.decreaseGeneration = true
            be.progress = 0
            be.maxProgress = 0
        }

        @OnlyIn(Dist.CLIENT)
        @JvmStatic
        private fun processRot(be: MithrilineFurnaceEntity) {
            be.previousRot = be.rotAngle

            if (be.successfulStructure) {
                be.rotAngle += 45f * (1f / 20f)
            } else if (be.rotAngle % 90 != 0f) {
                be.rotAngle += 45f * (1f / 20f) / 2

                if (be.rotAngle % 90 == 0f) be.rotAngle = 90f * floor(be.rotAngle / 90)
            }
        }
    }
}
