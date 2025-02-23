package team._0mods.ecr.common.blocks.entity

import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.world.MenuProvider
import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ContainerData
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import ru.hollowhorizon.hc.client.utils.get
import ru.hollowhorizon.hc.common.capabilities.CapabilityInstance
import ru.hollowhorizon.hc.common.capabilities.HollowCapabilityV2
import ru.hollowhorizon.hc.common.capabilities.containers.HollowContainer
import ru.hollowhorizon.hc.common.capabilities.containers.container
import ru.hollowhorizon.hc.common.objects.blocks.HollowBlockEntity
import team._0mods.ecr.api.block.StructuralPosition
import team._0mods.ecr.api.block.inventory.WrappedHollowInventory
import team._0mods.ecr.api.item.ItemStorage
import team._0mods.ecr.api.mru.MRUHolder
import team._0mods.ecr.api.mru.MRUStorage
import team._0mods.ecr.api.mru.MRUTypes
import team._0mods.ecr.api.utils.StackHelper
import team._0mods.ecr.common.api.ContainerLevelAccess
import team._0mods.ecr.common.init.config.ECCommonConfig
import team._0mods.ecr.common.init.registry.ECRMultiblocks
import team._0mods.ecr.common.init.registry.ECRegistry
import team._0mods.ecr.common.menu.MithrilineFurnaceMenu
import kotlin.math.floor

class MithrilineFurnaceEntity(pos: BlockPos, state: BlockState) : HollowBlockEntity(ECRegistry.mithrilineFurnaceEntity.get(), pos, state), MenuProvider,
    MRUHolder {
    private val containerData: ContainerData = object : ContainerData {
        override fun get(index: Int): Int = when (index) {
            0 -> this@MithrilineFurnaceEntity.craftProgress
            1 -> this@MithrilineFurnaceEntity.maxCraftProgress
            else -> 0
        }

        override fun set(index: Int, value: Int) {
            when (index) {
                0 -> this@MithrilineFurnaceEntity.craftProgress = value
                1 -> this@MithrilineFurnaceEntity.maxCraftProgress = value
            }
        }

        override fun getCount(): Int = 2
    }

    var structureIsValid = false
    var receivingTicks = 0
    var craftProgress = 0
    var maxCraftProgress = 0
    var slownessGeneration = false
    // Client Only
    var coreRotationPrevious = 0f
    var coreRotationAngle = 0f

    override fun saveAdditional(tag: CompoundTag) {
        tag.putBoolean("FullStructure", this.structureIsValid)
        tag.putBoolean("SlownessGeneration", this.slownessGeneration)
        tag.putInt("Progress", this.craftProgress)
        tag.putInt("MaxProgress", this.maxCraftProgress)
        super.saveAdditional(tag)
    }

    override fun load(tag: CompoundTag) {
        this.structureIsValid = tag.getBoolean("FullStructure")
        this.slownessGeneration = tag.getBoolean("DecreaseGeneration")
        this.craftProgress = tag.getInt("Progress")
        this.maxCraftProgress = tag.getInt("MaxProgress")
        super.load(tag)
    }

    override fun createMenu(id: Int, inv: Inventory, player: Player): AbstractContainerMenu? {
        return MithrilineFurnaceMenu(
            id,
            inv,
            this[ItemContainer::class].items,
            this,
            ContainerLevelAccess(this.level ?: return null, this.blockPos),
            this.containerData
        )
    }

    override fun getDisplayName(): Component = Component.empty()

    override val mruContainer: MRUStorage = this[MRUContainer::class]

    override val holderType: MRUHolder.MRUHolderType = MRUHolder.MRUHolderType.RECEIVER

    @HollowCapabilityV2(MithrilineFurnaceEntity::class)
    class ItemContainer: CapabilityInstance(), ItemStorage {
        private val container = HollowContainer(this, 2) { slot, _ -> slot != 1 }

        override val items by container(WrappedHollowInventory(container, this, { it == 1 }) { i, _ -> i == 0 })
    }

    @HollowCapabilityV2(MithrilineFurnaceEntity::class)
    class MRUContainer: CapabilityInstance(), MRUStorage {
        override var mru: Int by syncable(0)
        override val maxMRUStorage: Int = 10000

        override val mruType: MRUTypes = MRUTypes.ESPE
    }

    companion object {
        @JvmField
        val CRYSTAL_POSITION = makePositions()

        @JvmStatic
        fun getActiveCollectors(level: Level, pos: BlockPos): Int {
            val coll = CRYSTAL_POSITION?.get(pos)?.filter { level.getBlockState(it).block == ECRegistry.mithrilineCrystal.get() }
            return coll?.size ?: 0
        }

        @JvmStatic
        fun onTick(level: Level, pos: BlockPos, state: BlockState, be: MithrilineFurnaceEntity) {
            be.structureIsValid = ECRMultiblocks.mithrilineFurnace.get().isValid(level, pos)
            if (level.isClientSide) {
                be.processRotation()
                return
            }

            if (be.structureIsValid) {
                be.generateESPE(level, pos)
                be.processRecipeIfPresent(level)
            } else be.resetProgress()
        }

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
        private fun MithrilineFurnaceEntity.generateESPE(level: Level, pos: BlockPos) {
            val collectors = getActiveCollectors(level, pos)
            val config = ECCommonConfig.instance.mithrilineFurnaceConfig

            if (collectors > 0 && (this.receivingTicks++ % (160 / collectors) == 0)) {
                var collect = collectors * 2
                if (this.slownessGeneration) collect /= config.generationReductionLevel
                this.mruContainer.receiveMru(collect)
            } else {
                val c = config.receiveESPEWhenCrystalsInUnavailable
                if (c > 0) {
                    if (CRYSTAL_POSITION == null && (this.receivingTicks++ % 160 == 0)) {
                        this.mruContainer.receiveMru(c)
                    }
                }
            }
        }

        @JvmStatic
        private fun MithrilineFurnaceEntity.processRecipeIfPresent(level: Level) {
            val container = this[ItemContainer::class].items
            val input = container.getItem(0)
            if (input.isEmpty) {
                this.resetProgress()
                return
            }

            val inv = SimpleContainer(1).apply { this.setItem(0, input) }
            val recipe = level.recipeManager.getRecipeFor(ECRegistry.mithrilineFurnaceRecipe.get(), inv, level)

            if (recipe.isPresent) {
                val mfr = recipe.get()
                val result = mfr.getResultItem(level.registryAccess())
                val ingredientCount = mfr.ingredients[0].items[0].count

                this.slownessGeneration = true
                this.maxCraftProgress = mfr.espe

                if (StackHelper.canCombine(result.copy(), container.getItem(1), input.count, ingredientCount)) {
                    this.processTick(mfr.espe)
                    if (this.craftProgress >= mfr.espe) {
                        inv.clearContent()
                        container.removeItem(0, ingredientCount)
                        if (container.getItem(1).isEmpty)
                            container.setItem(1, result.copy())
                        else container.getItem(1).grow(result.count)
                        resetProgress()
                    }
                }
            } else {
                inv.clearContent()
                resetProgress()
            }
        }

        @JvmStatic
        private fun MithrilineFurnaceEntity.processTick(mru: Int) {
            val storage = this.mruContainer
            val extractionStep = (1..1000).reversed().firstOrNull { this.checkExtract(mru, it) } ?: 0
            storage.extractMru(extractionStep)
            this.craftProgress += extractionStep
            this.setChanged()
        }

        @JvmStatic
        private fun MithrilineFurnaceEntity.checkExtract(mru: Int, max: Int): Boolean {
            val storage = this.mruContainer
            return storage.canExtract(max) && (mru >= (max + this.craftProgress))
        }

        @JvmStatic
        private fun MithrilineFurnaceEntity.resetProgress() {
            if (this.slownessGeneration) this.slownessGeneration = false
            this.craftProgress = 0
            this.maxCraftProgress = 0
            this.setChanged()
        }

        // Client only
        @JvmStatic
        private fun MithrilineFurnaceEntity.processRotation() {
            this.coreRotationPrevious = this.coreRotationAngle

            if (this.structureIsValid) {
                this.coreRotationAngle += 45f * (1f / 20)
            } else if (this.coreRotationAngle % 90 != 0f) {
                this.coreRotationAngle += 45f * (1f / 20) / 2

                if (this.coreRotationAngle % 90 == 0f) this.coreRotationAngle = 90f * floor(this.coreRotationAngle / 90)
            }
        }
    }
}
