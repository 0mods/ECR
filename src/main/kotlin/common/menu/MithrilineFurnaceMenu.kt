package team._0mods.ecr.common.menu

import net.minecraft.network.FriendlyByteBuf
import net.minecraft.world.Container
import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.ContainerData
import net.minecraft.world.inventory.ContainerLevelAccess
import net.minecraft.world.inventory.SimpleContainerData
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity
import team._0mods.ecr.api.container.AbstractMenu
import team._0mods.ecr.api.container.slot.VanillaSpecialSlot
import team._0mods.ecr.common.init.registry.ECRRegistry

class MithrilineFurnaceMenu(
    containerId: Int,
    inv: Inventory,
    container: Container,
    val blockEntity: BlockEntity?,
    access: ContainerLevelAccess,
    val data: ContainerData
): AbstractMenu(ECRRegistry.mithrilineFurnaceMenu, containerId, access) {
    constructor(containerId: Int, inventory: Inventory, buffer: FriendlyByteBuf):
            this(containerId, inventory, SimpleContainer(2), inventory.player.commandSenderWorld.getBlockEntity(buffer.readBlockPos()), ContainerLevelAccess.NULL, SimpleContainerData(2))

    init {
        addSlot(VanillaSpecialSlot(container, 0, 80, 60))
        addSlot(VanillaSpecialSlot(container, 1, 80, 22, { false }))

        inv.make()

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

    override fun stillValid(player: Player): Boolean = stillValid(this.access, player, ECRRegistry.mithrilineFurnace)
}
