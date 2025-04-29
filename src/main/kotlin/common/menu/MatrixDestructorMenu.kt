package team._0mods.ecr.common.menu

import net.minecraft.network.FriendlyByteBuf
import net.minecraft.world.Container
import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.ContainerLevelAccess
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity
import team._0mods.ecr.api.container.AbstractMenu
import team._0mods.ecr.api.container.slot.VanillaSpecialSlot
import team._0mods.ecr.api.item.SoulStoneLike
import team._0mods.ecr.api.utils.SoulStoneUtils.owner
import team._0mods.ecr.common.init.registry.ECRegistry

class MatrixDestructorMenu(
    containerId: Int,
    inv: Inventory,
    container: Container,
    val blockEntity: BlockEntity?,
    access: ContainerLevelAccess
) : AbstractMenu(
    ECRegistry.matrixDestructorMenu, containerId, access
) {
    constructor(containerId: Int, inv: Inventory, buf: FriendlyByteBuf): this(
        containerId, inv, SimpleContainer(1), inv.player.commandSenderWorld.getBlockEntity(buf.readBlockPos()), ContainerLevelAccess.NULL
    )

    init {
        addSlot(VanillaSpecialSlot(container, 0, 80, 60, {
            if (it.item is SoulStoneLike) it.owner != null
            else false
        }))
        inv.make()
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

    override fun stillValid(player: Player): Boolean = stillValid(this.access, player, ECRegistry.matrixDestructor)
}