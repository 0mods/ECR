package team._0mods.ecr.common.container

import net.minecraft.network.FriendlyByteBuf
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.ContainerLevelAccess
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.ItemStackHandler
import team._0mods.ecr.common.init.registry.ECRegistry
import team._0mods.ecr.api.container.AbstractContainer
import team._0mods.ecr.api.container.slot.SpecialSlot

class MithrilineFurnaceContainer(
    containerId: Int,
    inv: Inventory,
    container: IItemHandler,
    val blockEntity: BlockEntity?,
    access: ContainerLevelAccess
) : AbstractContainer(ECRegistry.mithrilineFurnaceContainer.get(), containerId, access) {
    constructor(containerId: Int, inv: Inventory, buf: FriendlyByteBuf):
            this(containerId, inv, ItemStackHandler(2), inv.player.commandSenderWorld.getBlockEntity(buf.readBlockPos()), ContainerLevelAccess.NULL)

    init {
        addSlot(SpecialSlot(container, 0, 80, 60))
        addSlot(SpecialSlot(container, 1, 80, 30, { false }))

        makeInv(inv, 8, 84)
    }

    override fun quickMoveStack(player: Player, index: Int): ItemStack {
        var qms = ItemStack.EMPTY
        val ms = this.slots[index]

        if (ms.hasItem()) {
            val raw = ms.item

            qms = raw.copy()

            if (index == 1) {
                // try to move to hotbar
                if (!this.moveItemStackTo(raw, 2, 38, true))
                    return ItemStack.EMPTY

                ms.onQuickCraft(raw, qms)
            } else if (index in 2 ..< 38) {
                // try to move from inventory to craft slot
                if (!this.moveItemStackTo(raw, 0, 1, false)) {
                    if (index in 12 ..< 38) {
                        // try to move to hotbar
                        if (!this.moveItemStackTo(raw, 2, 11, false))
                            return ItemStack.EMPTY
                        // try to move to inventory no hotbar
                    } else if (!this.moveItemStackTo(raw, 12, 38, false))
                        return ItemStack.EMPTY
                }
                // try to move to inventory
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
}