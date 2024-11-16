package team._0mods.ecr.common.container

import net.minecraft.core.BlockPos
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.ContainerLevelAccess
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.ItemStackHandler
import team._0mods.ecr.api.container.AbstractContainer
import team._0mods.ecr.api.container.slot.SpecialSlot
import team._0mods.ecr.api.item.BoundGem
import team._0mods.ecr.common.blocks.entity.XLikeBlockEntity
import team._0mods.ecr.common.init.registry.ECRegistry

abstract class XLikeMenu(
    type: MenuType<*>,
    containerId: Int,
    inv: Inventory,
    container: IItemHandler,
    val blockEntity: XLikeBlockEntity?,
    access: ContainerLevelAccess,
    settings: Settings.() -> Unit = {}
): AbstractContainer(type, containerId, access) {
    companion object {
        protected val be = { l: Level, bp: BlockPos ->
            val be = l.getBlockEntity(bp)
            if (be is XLikeBlockEntity) be
            else null
        }
    }

    init {
        val s = Settings().apply(settings)

        // Recipe slots
        addSlot(SpecialSlot(container, 0, 26, 17, s.place, s.pickup, s.stackSize))
        addSlot(SpecialSlot(container, 1, 62, 17, s.place, s.pickup, s.stackSize))
        addSlot(SpecialSlot(container, 2, 26, 53, s.place, s.pickup, s.stackSize))
        addSlot(SpecialSlot(container, 3, 62, 53, s.place, s.pickup, s.stackSize))

        // Catalyst
        addSlot(SpecialSlot(container, 4, 44, 35, s.place, s.pickup, s.stackSize))
        // Result
        addSlot(SpecialSlot(container, 5, 116, 35, { false }, stackSize = s.stackSize))

        // Bound gem
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

    class Settings {
        var stackSize: Int = 64
        var place: SpecialSlot.(ItemStack) -> Boolean = { true }
        var pickup: SpecialSlot.(Player) -> Boolean = { !this.itemHandler.extractItem(indx, 1, true).isEmpty }
    }

    class Envoyer(
        containerId: Int,
        inv: Inventory,
        container: IItemHandler,
        blockEntity: XLikeBlockEntity?,
        access: ContainerLevelAccess,
    ): XLikeMenu(ECRegistry.envoyerMenu.get(), containerId, inv, container, blockEntity, access, {
        this.stackSize = 1
    }) {
        constructor(
            id: Int,
            inv: Inventory,
            buf: FriendlyByteBuf
        ): this(
            id,
            inv,
            ItemStackHandler(7),
            be(inv.player.commandSenderWorld, buf.readBlockPos()),
            ContainerLevelAccess.NULL,
        )

        override fun stillValid(player: Player): Boolean = stillValid(this.access, player, ECRegistry.envoyer.get())
    }

    class MagicTable(
        containerId: Int,
        inv: Inventory,
        container: IItemHandler,
        blockEntity: XLikeBlockEntity?,
        access: ContainerLevelAccess,
    ): XLikeMenu(ECRegistry.magicTableMenu.get(), containerId, inv, container, blockEntity, access) {
        constructor(
            id: Int,
            inv: Inventory,
            buf: FriendlyByteBuf
        ): this(
            id,
            inv,
            ItemStackHandler(7),
            be(inv.player.commandSenderWorld, buf.readBlockPos()),
            ContainerLevelAccess.NULL,
        )

        override fun stillValid(player: Player): Boolean = stillValid(this.access, player, ECRegistry.magicTable.get())
    }
}