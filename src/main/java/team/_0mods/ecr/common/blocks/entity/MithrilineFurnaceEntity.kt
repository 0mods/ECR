package team._0mods.ecr.common.blocks.entity

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.world.MenuProvider
import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ContainerData
import net.minecraft.world.inventory.ContainerLevelAccess
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ForgeCapabilities
import net.minecraftforge.common.util.LazyOptional
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.ItemStackHandler
import ru.hollowhorizon.hc.common.network.sendAllInDimension
import team._0mods.ecr.api.ModId
import team._0mods.ecr.api.block.StructuralPosition
import team._0mods.ecr.api.block.inventory.WrappedInventory
import team._0mods.ecr.api.mru.MRUContainer
import team._0mods.ecr.api.utils.StackHelper
import team._0mods.ecr.common.capability.MRUContainerImpl
import team._0mods.ecr.common.container.MithrilineFurnaceContainer
import team._0mods.ecr.common.init.config.ECCommonConfig
import team._0mods.ecr.common.init.registry.ECCapabilities
import team._0mods.ecr.common.init.registry.ECMultiblocks
import team._0mods.ecr.common.init.registry.ECRegistry
import team._0mods.ecr.common.particle.ECParticleOptions
import team._0mods.ecr.network.ClientMithrilineFurnaceUpdate
import java.awt.Color
import kotlin.math.floor

class MithrilineFurnaceEntity(pos: BlockPos, blockState: BlockState) :
    BlockEntity(ECRegistry.mithrilineFurnaceEntity.get(), pos, blockState), MenuProvider {
    private val itemHandler = object : ItemStackHandler(2) {
        override fun onContentsChanged(slot: Int) {
            setChanged()
        }
    }

    val mruContainer = MRUContainerImpl(MRUContainer.MRUType.ESPE, 10000, 0) {
        if (!level!!.isClientSide) {
            ClientMithrilineFurnaceUpdate(it.mruStorage, this.blockPos).sendAllInDimension(level!!)
            setChanged()
        }
    }

    private val containerData: ContainerData = object : ContainerData {
        override fun get(index: Int): Int = when(index) {
            0 -> this@MithrilineFurnaceEntity.progress
            1 -> this@MithrilineFurnaceEntity.maxProgress
            else -> 0
        }

        override fun set(index: Int, value: Int) {
            when(index) {
                0 -> this@MithrilineFurnaceEntity.progress = value
                1 -> this@MithrilineFurnaceEntity.maxProgress = value
            }
        }

        override fun getCount(): Int = 2
    }

    private var itemHandlerLazy = LazyOptional.empty<IItemHandler>()
    private var mruStorageLazy = LazyOptional.empty<MRUContainer>()
    private var wrappedHandlerLazy = LazyOptional.empty<WrappedInventory>()

    var successfulStructure = false
    var tickCount = 0
    var progress = 0
    var maxProgress = 0
    var canGenerate = true

    // Calculates only on a client
    @OnlyIn(Dist.CLIENT) var previousRot = 0f
    @OnlyIn(Dist.CLIENT) var rotAngle = 0f
    // end

    override fun onLoad() {
        super.onLoad()
        itemHandlerLazy = LazyOptional.of(::itemHandler)
        mruStorageLazy = LazyOptional.of(::mruContainer)
        wrappedHandlerLazy = LazyOptional.of { WrappedInventory(itemHandler, { it == 1 && successfulStructure }) { i, s -> i == 0 && itemHandler.isItemValid(i, s) && successfulStructure } }
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
        tag.putBoolean("CanGenerate", canGenerate)
        tag.putInt("Progress", progress)
        tag.putInt("MaxProgress", maxProgress)
        super.saveAdditional(tag)
    }

    override fun load(tag: CompoundTag) {
        itemHandler.deserializeNBT(tag.getCompound("ItemStorage"))
        mruContainer.deserializeNBT(tag.getCompound("ESPEStorage"))
        successfulStructure = tag.getBoolean("FullStructure")
        canGenerate = tag.getBoolean("CanGenerate")
        progress = tag.getInt("Progress")
        maxProgress = tag.getInt("MaxProgress")
        super.load(tag)
    }

    override fun createMenu(id: Int, inv: Inventory, player: Player): AbstractContainerMenu? {
        ClientMithrilineFurnaceUpdate(this.mruContainer.mruStorage, this.blockPos).sendAllInDimension(level!!)
        return MithrilineFurnaceContainer(id, inv, itemHandler, this, ContainerLevelAccess.create(this.level ?: return null, this.blockPos), this.containerData)
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

    override fun getUpdatePacket(): Packet<ClientGamePacketListener> = ClientboundBlockEntityDataPacket.create(this)

    fun getActiveCollectors(level: Level, pos: BlockPos): Int {
        val collectors = CRYSTAL_POSITION?.get(pos)?.filter { level.getBlockState(it).block == ECRegistry.mithrilineCrystal.get() }
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
            val complete = be.successfulStructure
            be.successfulStructure = ECMultiblocks.mithrilineFurnace.isComplete(level, pos)

            if (!level.isClientSide) {
                ClientMithrilineFurnaceUpdate(be.mruContainer.mruStorage, pos).sendAllInDimension(level)
                if (complete) {
                    generateESPE(level, pos, be)
                    processRecipeIfPresent(be, level)
                } else {
                    resetProgress(be)
                }
            } else {
                processRot(be)
            }

            if (complete) {
                level.addParticle(
                    ECParticleOptions(Color.GREEN, 0.5f, 10, 0.1f, true, false),
                    pos.x + 0.5, pos.y + 0.5, pos.z + 0.5, 1.0, 1.0, 1.0
                )
            }
        }

        @JvmStatic
        private fun generateESPE(level: Level, pos: BlockPos, be: MithrilineFurnaceEntity) {
            val collectors = be.getActiveCollectors(level, pos)

            if (collectors != 0 && (be.tickCount++ % (160 / collectors) == 0)) {
                var collect = collectors * 4 - 3 + 1

                if (!be.canGenerate) collect /= 4

                be.getCapability(ECCapabilities.MRU_CONTAINER).ifPresent { it.receiveMru(collect) }
            } else if (CRYSTAL_POSITION == null && (be.tickCount++ % 160 == 0)) {
                be.getCapability(ECCapabilities.MRU_CONTAINER).ifPresent { it.receiveMru(10) }
            }
        }

        @JvmStatic
        private fun hasRecipe(be: MithrilineFurnaceEntity, level: Level): Boolean {
            if (!be.itemHandler.getStackInSlot(0).isEmpty) {
                val inv = SimpleContainer(1).apply { this.setItem(0, be.itemHandler.getStackInSlot(0)) }
                val recipe = level.recipeManager.getRecipeFor(ECRegistry.mithrilineFurnaceRecipe.get(), inv, level)

                return recipe.isPresent
            }

            return false
        }

        @JvmStatic
        private fun processRecipeIfPresent(be: MithrilineFurnaceEntity, level: Level) {
            if (!be.itemHandler.getStackInSlot(0).isEmpty) {
                val inv = SimpleContainer(1).apply { this.setItem(0, be.itemHandler.getStackInSlot(0)) }
                if (hasRecipe(be, level)) {
                    val recipe = level.recipeManager.getRecipeFor(ECRegistry.mithrilineFurnaceRecipe.get(), inv, level)

                    val mfr = recipe.get()
                    val result = mfr.resultItem
                    val ingrCount = mfr.ingredients[0].items[0].count

                    be.canGenerate = false
                    be.maxProgress = mfr.espe

                    if (canCombine(result.copy(), be.itemHandler.getStackInSlot(1), inv.getItem(0).count, ingrCount)) {
                        if (mfr.espe > be.mruContainer.mruStorage) {
                            /*be.progress++
                            be.getCapability(ECCapabilities.MRU_CONTAINER).ifPresent { it.extractMru(1, false) }*/
                            processTick(be, mfr.espe)
                        } else if (be.mruContainer.mruStorage >= mfr.espe) {
                            be.progress = mfr.espe
                            be.getCapability(ECCapabilities.MRU_CONTAINER).ifPresent { it.extractMru(mfr.espe, false) }
                        }

                        if (be.progress >= mfr.espe) {
                            inv.clearContent()
                            be.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent {
                                it.extractItem(0, ingrCount, false)
                                it.insertItem(1, result.copy(), false)
                            }

                            resetProgress(be)
                        }
                    }
                } else {
                    inv.clearContent()
                    resetProgress(be)
                }
            } else {
                resetProgress(be)
            }
        }

        @JvmStatic
        private fun processTick(be: MithrilineFurnaceEntity, neededESPE: Int) {
            val storage = be.mruContainer

            if (be.checkExtraction(neededESPE, 1000)) {
                storage.extractMru(1000)
                be.progress += 1000
            } else if (be.checkExtraction(neededESPE, 100)) {
                storage.extractMru(100)
                be.progress += 100
            } else if (be.checkExtraction(neededESPE, 10)) {
                storage.extractMru(10)
                be.progress += 10
            } else {
                storage.extractMru(1)
                be.progress++
            }
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
            be.canGenerate = true
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
