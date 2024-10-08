package team._0mods.ecr.common.container

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
import team._0mods.ecr.api.item.BoundGem
import team._0mods.ecr.common.blocks.entity.EnvoyerBlockEntity
import team._0mods.ecr.common.init.registry.ECRegistry

class EnvoyerContainer(
    containerId: Int,
    inv: Inventory,
    container: IItemHandler,
    private val blockEntity: BlockEntity?,
    access: ContainerLevelAccess
) : AbstractContainer(
    ECRegistry.envoyerContainer.get(),
    containerId,
    access
) {
    constructor(containerId: Int, inv: Inventory, buf: FriendlyByteBuf):
            this(containerId, inv, ItemStackHandler(7), inv.player.level.getBlockEntity(buf.readBlockPos()), ContainerLevelAccess.NULL)

    val be: EnvoyerBlockEntity?
        get() {
            if (this.blockEntity is EnvoyerBlockEntity) return this.blockEntity
            return null
        }

    init {
        addSlot(SpecialSlot(container, 0, 26, 17, stackSize = 1))
        addSlot(SpecialSlot(container, 1, 62, 17, stackSize = 1))
        addSlot(SpecialSlot(container, 2, 44, 35, stackSize = 1))
        addSlot(SpecialSlot(container, 3, 26, 53, stackSize = 1))
        addSlot(SpecialSlot(container, 4, 62, 53, stackSize = 1))
        addSlot(SpecialSlot(container, 5, 116, 35, { false }, stackSize = 1))

        addSlot(
            SpecialSlot(
                container, 6, 152, 53, {
                    val item = it.item
                    item is BoundGem && item.getBoundPos(it) != null
                }
            )
        )

        makeInv(inv, 8, 84)
    }

    override fun quickMoveStack(player: Player, index: Int): ItemStack {
        var qms = ItemStack.EMPTY
        val ms = this.slots[index]

        if (ms.hasItem()) {
            val raw = ms.item

            qms = raw.copy()

            if (index == 0) {
                if (!this.moveItemStackTo(raw, 7, 43, true))
                    return ItemStack.EMPTY
            } else if (index in 7 ..< 43) {
                if (!this.moveItemStackTo(raw, 0, 6, false)) {
                    if (index in 17 ..< 43) {
                        if (!this.moveItemStackTo(raw, 7, 16, false))
                            return ItemStack.EMPTY
                    } else if (!this.moveItemStackTo(raw, 17, 43, false))
                        return ItemStack.EMPTY
                }
            } else if (!this.moveItemStackTo(raw, 7, 43, false))
                return ItemStack.EMPTY

            if (raw.isEmpty) ms.set(ItemStack.EMPTY)
            else ms.setChanged()

            if (raw.count == qms.count) return ItemStack.EMPTY

            ms.onTake(player, raw)
        }

        return qms
    }

    override fun stillValid(player: Player): Boolean = stillValid(this.access, player, ECRegistry.envoyer.get())
}