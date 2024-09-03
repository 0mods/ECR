package team._0mods.ecr.common.blocks.entity

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ContainerLevelAccess
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ForgeCapabilities
import net.minecraftforge.common.util.LazyOptional
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.ItemStackHandler
import team._0mods.ecr.api.mru.MRUGenerator
import team._0mods.ecr.common.capability.MRUContainer
import team._0mods.ecr.common.capability.impl.MRUContainerImpl
import team._0mods.ecr.common.container.MatrixDestructorContainer
import team._0mods.ecr.common.init.config.ECCommonConfig
import team._0mods.ecr.common.init.registry.ECCapabilities
import team._0mods.ecr.common.init.registry.ECRegistry
import team._0mods.ecr.common.items.SoulStone
import team._0mods.ecr.network.ECNetworkManager.sendToClient
import team._0mods.ecr.network.packets.MatrixDestructorS2CUpdatePacket

class MatrixDestructorEntity(pos: BlockPos, blockState: BlockState): BlockEntity(ECRegistry.matrixDestructorEntity.get(), pos, blockState), MenuProvider, MRUGenerator {
    private val itemHandler = object : ItemStackHandler(1) {
        override fun onContentsChanged(slot: Int) {
            setChanged()
        }
    }

    val mruStorage = MRUContainerImpl(MRUContainer.MRUType.RADIATION_UNIT, 10000, 0) {
        if (!level!!.isClientSide) {
            MatrixDestructorS2CUpdatePacket(it.mruStorage, this.blockPos).sendToClient()
            setChanged()
        }
    }

    private var itemHandlerLazy = LazyOptional.empty<IItemHandler>()
    private var mruStorageLazy = LazyOptional.empty<MRUContainer>()

    var progress = 0

    override val currentMRUContainer: MRUContainer get() = mruStorage

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
        tag.put("MRUStorage", mruStorage.serializeNBT())
        tag.putInt("InjectionProgress", progress)
        super.saveAdditional(tag)
    }

    override fun load(tag: CompoundTag) {
        itemHandler.deserializeNBT(tag.getCompound("ItemStorage"))
        mruStorage.deserializeNBT(tag.getCompound("MRUStorage"))
        progress = tag.getInt("InjectionProgress")
        super.load(tag)
    }

    override fun <T : Any?> getCapability(cap: Capability<T>, side: Direction?): LazyOptional<T> {
        if (cap == ForgeCapabilities.ITEM_HANDLER) return itemHandlerLazy.cast()

        if (cap == ECCapabilities.MRU_CONTAINER) return mruStorageLazy.cast()

        return super.getCapability(cap, side)
    }

    override fun getUpdatePacket(): Packet<ClientGamePacketListener> = ClientboundBlockEntityDataPacket.create(this)

    override fun createMenu(id: Int, inv: Inventory, arg2: Player): AbstractContainerMenu? {
        return MatrixDestructorContainer(id, inv, this.itemHandler, this, ContainerLevelAccess.create(this.level ?: return null, this.blockPos))
    }

    override fun getDisplayName(): Component = Component.empty()

    companion object {
        @JvmStatic
        fun onTick(level: Level, pos: BlockPos, state: BlockState, be: MatrixDestructorEntity) {
            val convertCost = ECCommonConfig.instance.matrixConsuming
            val receiveCost = ECCommonConfig.instance.matrixResult

            if (!level.isClientSide) {
                MatrixDestructorS2CUpdatePacket(be.mruStorage.mruStorage, be.blockPos).sendToClient()
                val stack = be.itemHandler.getStackInSlot(0)

                if (!stack.isEmpty) {
                    if (stack.item is SoulStone) {
                        val i = stack.item as SoulStone
                        val storage = i.getCapacity(stack)

                        if (storage - receiveCost >= 0) {
                            if (be.mruStorage.mruStorage < be.mruStorage.maxMRUStorage) {
                                if (storage >= convertCost) {
                                    i.remove(stack, convertCost)
                                    be.progress = convertCost
                                } else {
                                    i.remove(stack, 1)
                                    be.progress++
                                }

                                if (be.progress >= convertCost) {
                                    be.progress = 0
                                    be.mruStorage.receiveMru(receiveCost)
                                    be.setChanged()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}