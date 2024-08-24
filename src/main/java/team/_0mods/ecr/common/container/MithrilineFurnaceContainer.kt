package team._0mods.ecr.common.container

import net.minecraft.network.FriendlyByteBuf
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.ContainerData
import net.minecraft.world.inventory.ContainerLevelAccess
import net.minecraft.world.inventory.SimpleContainerData
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.ItemStackHandler
import team._0mods.ecr.api.container.AbstractContainer
import team._0mods.ecr.api.container.slot.SpecialSlot
import team._0mods.ecr.common.init.registry.ECRegistry

class MithrilineFurnaceContainer(
    containerId: Int,
    inv: Inventory,
    container: IItemHandler,
    val blockEntity: BlockEntity?,
    access: ContainerLevelAccess,
    val data: ContainerData
) : AbstractContainer(ECRegistry.mithrilineFurnaceContainer.get(), containerId, access) {
    constructor(containerId: Int, inv: Inventory, buf: FriendlyByteBuf):
            this(containerId, inv, ItemStackHandler(2), inv.player.commandSenderWorld.getBlockEntity(buf.readBlockPos()), ContainerLevelAccess.NULL, SimpleContainerData(2))

    init {
        addSlot(SpecialSlot(container, 0, 80, 60))
        addSlot(SpecialSlot(container, 1, 80, 22, { false }))

        makeInv(inv, 8, 84)

        addDataSlots(data)
    }

    override fun quickMoveStack(player: Player, index: Int): ItemStack {
        var qms = ItemStack.EMPTY
        val ms = this.slots[index]

        if (ms.hasItem()) {
            val raw = ms.item

            qms = raw.copy()

            if (index == 1) {
                if (!this.moveItemStackTo(raw, 2, 38, true))
                    return ItemStack.EMPTY

                ms.onQuickCraft(raw, qms)
            } else if (index in 2 ..< 38) {
                if (!this.moveItemStackTo(raw, 0, 1, false)) {
                    if (index in 12 ..< 38) {
                        if (!this.moveItemStackTo(raw, 2, 11, false))
                            return ItemStack.EMPTY
                    } else if (!this.moveItemStackTo(raw, 12, 38, false))
                        return ItemStack.EMPTY
                }
            } else if (!this.moveItemStackTo(raw, 2, 38, false))
                return ItemStack.EMPTY

            if (raw.isEmpty) ms.set(ItemStack.EMPTY)
            else ms.setChanged()

            if (raw.count == qms.count) return ItemStack.EMPTY

            ms.onTake(player, raw)
        }

        return qms
    }

    override fun stillValid(player: Player): Boolean =
        stillValid(this.access, player, ECRegistry.mithrilineFurnace.first)

    val hasActiveRecipe = this.data.get(1) > 0

    fun scaleProgress(): Int {
        val progress = this.data.get(0)
        val maxProgress = this.data.get(1)
        return if (progress != 0 && maxProgress != 0)
            -(progress * 16 / maxProgress)
        else 0
    }
}