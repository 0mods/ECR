package team._0mods.ecr.common.blocks.entity

import net.minecraft.core.BlockPos
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
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ForgeCapabilities
import net.minecraftforge.common.util.LazyOptional
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.ItemStackHandler
import team._0mods.ecr.ModId
import team._0mods.ecr.api.block.StructuralPosition
import team._0mods.ecr.api.utils.StackHelper
import team._0mods.ecr.common.capability.MRUContainer
import team._0mods.ecr.common.capability.impl.MRUContainerImpl
import team._0mods.ecr.common.container.MithrilineFurnaceContainer
import team._0mods.ecr.common.init.config.ECCommonConfig
import team._0mods.ecr.common.init.registry.ECCapabilities
import team._0mods.ecr.common.init.registry.ECMultiblocks
import team._0mods.ecr.common.init.registry.ECRegistry
import team._0mods.ecr.network.ECNetworkManager.sendToClient
import team._0mods.ecr.network.packets.MithrilineFurnaceS2CUpdatePacket
import kotlin.math.floor

class MithrilineFurnaceEntity(pos: BlockPos, blockState: BlockState) :
    BlockEntity(ECRegistry.mithrilineFurnace.second, pos, blockState), MenuProvider {
    companion object {
        @JvmField
        val CRYSTAL_POSITION = makePositions()

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
                if (complete) {
                    val collectors = be.getActiveCollectors(level, pos)

                    if (collectors != 0 && (be.tickCount++ % (160 / collectors) == 0)) {
                        var collect = collectors * 4 - 3 + 1

                        if (!be.notFrozenMRGeneration) collect /= 4

                        be.getCapability(ECCapabilities.MRU_CONTAINER)
                            .ifPresent {
                                it.receiveMru(collect)
                            }
                    } else if (CRYSTAL_POSITION == null) {
                        if (be.tickCount++ % 160 == 0) {
                            be.getCapability(ECCapabilities.MRU_CONTAINER).ifPresent {
                                it.receiveMru(14)
                            }
                        }
                    }

                    if (!be.itemHandler.getStackInSlot(0).isEmpty) {
                        val inv = SimpleContainer(1).apply {
                            this.setItem(0, be.itemHandler.getStackInSlot(0))
                        }

                        val recipe = level.recipeManager.getRecipeFor(ECRegistry.mithrilineFurnaceRecipe.get(), inv, level)
                        if (recipe.isPresent) {
                            val mfr = recipe.get()
                            val result = mfr.resultItem
                            val ingrCount = mfr.ingredients[0].items[0].count

                            be.notFrozenMRGeneration = false

                            be.containerData.set(1, mfr.espe)

                            if (
                                StackHelper.canCombineStacks(result.copy(), be.itemHandler.getStackInSlot(1)) &&
                                inv.getItem(0).count >= ingrCount
                            ) {
                                be.containerData.set(0, be.progress)

                                if (mfr.espe > be.mruStorage.mruStorage) {
                                    be.progress++
                                    be.containerData.set(0, be.progress)
                                    be.getCapability(ECCapabilities.MRU_CONTAINER).ifPresent { it.extractMru(1, false) }
                                } else if (be.mruStorage.mruStorage >= mfr.espe) {
                                    be.progress = mfr.espe
                                    be.containerData.set(0, be.progress)
                                    be.getCapability(ECCapabilities.MRU_CONTAINER).ifPresent { it.extractMru(mfr.espe, false) }
                                }

                                if (be.progress >= mfr.espe) {
                                    be.progress = 0
                                    inv.clearContent()
                                    be.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent {
                                        it.extractItem(0, ingrCount, false)
                                        it.insertItem(1, result.copy(), false)
                                    }

                                    be.setChanged()
                                    be.maxProgress = 0
                                    be.containerData.set(0, 0)
                                    be.containerData.set(1, 0)
                                }
                            }
                        } else {
                            inv.clearContent()
                            be.notFrozenMRGeneration = true
                            be.progress = 0
                            be.maxProgress = 0
                        }
                    } else {
                        be.notFrozenMRGeneration = true
                        be.progress = 0
                        be.maxProgress = 0
                    }
                } else {
                    be.notFrozenMRGeneration = true
                    be.progress = 0
                    be.maxProgress = 0
                }
            } else {
                be.previousRot = be.rotAngle

                if (complete) {
                    be.rotAngle += 45f * (1f / 20f)
                } else if (be.rotAngle % 90 != 0f) {
                    be.rotAngle += 45f * (1f / 20f) / 2

                    if (be.rotAngle % 90 == 0f) be.rotAngle = 90f * floor(be.rotAngle / 90)
                }
            }
        }
    }

    private val itemHandler = createStackHandler()
    val mruStorage = MRUContainerImpl(MRUContainer.MRUType.ESPE, 10000, 0) {
        if (!level!!.isClientSide) {
            MithrilineFurnaceS2CUpdatePacket(it.mruStorage, this.blockPos).sendToClient()
            setChanged()
        }
    }

    val containerData: ContainerData = object : ContainerData {
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

    var successfulStructure = false
    var tickCount = 0
    var progress = 0
    var maxProgress = 0
    var notFrozenMRGeneration = true

    // Calculates only on a client
    var previousRot = 0f
    var rotAngle = 0f
    // end

    override fun onLoad() {
        super.onLoad()
        itemHandlerLazy = LazyOptional.of(::itemHandler)
        mruStorageLazy = LazyOptional.of(::mruStorage)
    }

    override fun invalidateCaps() {
        super.invalidateCaps()
        itemHandlerLazy.invalidate()
        mruStorageLazy.invalidate()
    }

    override fun saveAdditional(tag: CompoundTag) {
        tag.put("ItemStorage", itemHandler.serializeNBT())
        tag.put("ESPEStorage", mruStorage.serializeNBT())
        tag.putBoolean("FullStructure", successfulStructure)
        tag.putBoolean("CanGenerate", notFrozenMRGeneration)
        super.saveAdditional(tag)
    }

    override fun load(tag: CompoundTag) {
        itemHandler.deserializeNBT(tag.getCompound("ItemStorage"))
        mruStorage.deserializeNBT(tag.getCompound("ESPEStorage"))
        successfulStructure = tag.getBoolean("FullStructure")
        notFrozenMRGeneration = tag.getBoolean("CanGenerate")
        super.load(tag)
    }

    override fun createMenu(id: Int, inv: Inventory, player: Player): AbstractContainerMenu? {
        MithrilineFurnaceS2CUpdatePacket(this.mruStorage.mruStorage, this.blockPos).sendToClient()
        return MithrilineFurnaceContainer(id, inv, itemHandler, this, ContainerLevelAccess.create(this.level ?: return null, this.blockPos), this.containerData)
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

    override fun getUpdatePacket(): Packet<ClientGamePacketListener> = ClientboundBlockEntityDataPacket.create(this)

    fun getActiveCollectors(level: Level, pos: BlockPos): Int {
        val collectors = CRYSTAL_POSITION?.get(pos)?.filter { level.getBlockState(it).block == ECRegistry.mithrilineCrystal.get() }
        if (collectors.isNullOrEmpty()) return 0
        return collectors.size
    }

    private fun createStackHandler() = object : ItemStackHandler(2) {
        override fun onContentsChanged(slot: Int) {
            setChanged()
        }
    }
}
