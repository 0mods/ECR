package team._0mods.ecr.common.container

import net.minecraft.core.BlockPos
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.ContainerLevelAccess
import net.minecraft.world.item.ItemStack
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.ItemStackHandler
import net.minecraftforge.items.SlotItemHandler
import team._0mods.ecr.common.init.registry.ECRegistry
import team._0mods.ecr.common.utils.container.AbstractContainer
import team._0mods.ecr.common.utils.container.slot.OutputSlotItemHandler

class MithrilineFurnaceContainer(
    containerId: Int,
    inv: Inventory,
    pos: BlockPos,
    container: IItemHandler,
    access: ContainerLevelAccess
) : AbstractContainer(ECRegistry.mithrilineFurnaceContainer.get(), containerId, inv, pos, inv.player.commandSenderWorld, container, access) {
    constructor(containerId: Int, inv: Inventory, buf: FriendlyByteBuf):
            this(containerId, inv, buf.readBlockPos(), ItemStackHandler(2), ContainerLevelAccess.NULL)

    init {
        addSlot(SlotItemHandler(container, 0, 80, 60))
        addSlot(OutputSlotItemHandler(container, 1, 80, 30))

        makeInv(inv, 8, 84)
    }

    override fun quickMoveStack(player: Player, index: Int): ItemStack {
        var qms = ItemStack.EMPTY
        val qmsl = this.slots[index]

        if (qmsl.hasItem()) {
            val raw = qmsl.item

            qms = raw.copy()

            if (index == 1) {
                if (!this.moveItemStackTo(raw, 2, 38, true))
                    return ItemStack.EMPTY

                qmsl.onQuickCraft(raw, qms)
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

            if (raw.isEmpty) qmsl.set(ItemStack.EMPTY)
            else qmsl.setChanged()

            if (raw.count == qms.count) return ItemStack.EMPTY

            qmsl.onTake(player, raw)
        }

        return qms
    }

    override fun stillValid(player: Player): Boolean =
        stillValid(this.access, player, ECRegistry.mithrilineFurnace.first)
}