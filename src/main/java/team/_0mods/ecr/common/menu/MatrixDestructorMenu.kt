package team._0mods.ecr.common.menu

import net.minecraft.network.FriendlyByteBuf
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.ContainerLevelAccess
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.ItemStackHandler
import team._0mods.ecr.api.container.AbstractContainer
import team._0mods.ecr.api.container.slot.SpecialSlot
import team._0mods.ecr.common.init.registry.ECRegistry
import team._0mods.ecr.common.items.SoulStone

class MatrixDestructorMenu(
    containerId: Int,
    inv: Inventory,
    container: IItemHandler,
    val blockEntity: BlockEntity?,
    access: ContainerLevelAccess
) : AbstractContainer(
    ECRegistry.matrixDestructorMenu.get(), containerId, access
) {
    constructor(containerId: Int, inv: Inventory, buf: FriendlyByteBuf): this(
        containerId, inv, ItemStackHandler(1), inv.player.commandSenderWorld.getBlockEntity(buf.readBlockPos()), ContainerLevelAccess.NULL
    )

    init {
        addSlot(SpecialSlot(container, 0, 80, 60, {
            if (it.item is SoulStone) {
                val ss = it.item as SoulStone
                ss.getOwner(it) != null
            } else false
        }))
        makeInv(inv, 8, 84)
    }

    override fun quickMoveStack(player: Player, index: Int): ItemStack {
        var qms = ItemStack.EMPTY
        val ms = this.slots[index]

        if (ms.hasItem()) {
            val raw = ms.item

            qms = raw.copy()

            if (index == 0) {
                if (!this.moveItemStackTo(raw, 1, 37, true))
                    return ItemStack.EMPTY
            } else if (index in 1 ..< 37) {
                if (!this.moveItemStackTo(raw, 0, 1, false)) {
                    if (index in 11 ..< 37) {
                        if (!this.moveItemStackTo(raw, 1, 10, false))
                            return ItemStack.EMPTY
                    } else if (!this.moveItemStackTo(raw, 11, 37, false))
                        return ItemStack.EMPTY
                }
            } else if (!this.moveItemStackTo(raw, 1, 37, false))
                return ItemStack.EMPTY

            if (raw.isEmpty) ms.set(ItemStack.EMPTY)
            else ms.setChanged()

            if (raw.count == qms.count) return ItemStack.EMPTY

            ms.onTake(player, raw)
        }

        return qms
    }

    override fun stillValid(player: Player): Boolean = stillValid(this.access, player, ECRegistry.matrixDestructor.get())
}