package team._0mods.ecr.common.menu

import net.minecraft.core.BlockPos
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.world.Container
import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.ContainerData
import net.minecraft.world.inventory.ContainerLevelAccess
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.inventory.SimpleContainerData
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import team._0mods.ecr.api.container.AbstractMenu
import team._0mods.ecr.api.container.slot.VanillaSpecialSlot
import team._0mods.ecr.api.item.BoundGem
import team._0mods.ecr.common.blocks.entity.XLikeBlockEntity
import team._0mods.ecr.common.init.registry.ECRegistry

abstract class XLikeMenu(
    type: MenuType<*>,
    containerId: Int,
    inv: Inventory,
    container: Container,
    val blockEntity: XLikeBlockEntity?,
    access: ContainerLevelAccess,
    val data: ContainerData
): AbstractMenu(type, containerId, access) {
    companion object {
        protected val be = { l: Level, bp: BlockPos ->
            val be = l.getBlockEntity(bp)
            if (be is XLikeBlockEntity) be
            else null
        }
    }

    protected fun buildSlots(container: Container, inv: Inventory, stackSize: Int = 64) {
        // Recipe slots
        addSlot(VanillaSpecialSlot(container, 0, 26, 17, stackSize =  stackSize))
        addSlot(VanillaSpecialSlot(container, 1, 62, 17, stackSize =  stackSize))
        addSlot(VanillaSpecialSlot(container, 2, 26, 53, stackSize =  stackSize))
        addSlot(VanillaSpecialSlot(container, 3, 62, 53, stackSize =  stackSize))

        // Catalyst
        addSlot(VanillaSpecialSlot(container, 4, 44, 35, stackSize =  stackSize))
        // Result
        addSlot(VanillaSpecialSlot(container, 5, 116, 35, { false }))

        // Bound gem
        addSlot(
            VanillaSpecialSlot(
                container, 6, 152, 53, {
                    val item = it.item
                    item is BoundGem && item.getBoundPos(it) != null
                }
            )
        )

        inv.make()

        addDataSlots(data)
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

    class Envoyer(
        containerId: Int,
        inv: Inventory,
        container: Container,
        blockEntity: XLikeBlockEntity?,
        access: ContainerLevelAccess,
        data: ContainerData
    ): XLikeMenu(ECRegistry.envoyerMenu, containerId, inv, container, blockEntity, access, data) {
        init {
            buildSlots(container, inv, 1)
        }

        constructor(
            id: Int,
            inv: Inventory,
            buf: FriendlyByteBuf
        ): this(
            id,
            inv,
            SimpleContainer(7),
            be(inv.player.commandSenderWorld, buf.readBlockPos()),
            ContainerLevelAccess.NULL,
            SimpleContainerData(2)
        )

        override fun stillValid(player: Player): Boolean = stillValid(this.access, player, ECRegistry.envoyer)
    }

    class MagicTable(
        containerId: Int,
        inv: Inventory,
        container: Container,
        blockEntity: XLikeBlockEntity?,
        access: ContainerLevelAccess,
        data: ContainerData
    ): XLikeMenu(ECRegistry.magicTableMenu, containerId, inv, container, blockEntity, access, data) {
        init {
            buildSlots(container, inv)

            addSlot(VanillaSpecialSlot(container, 7, 152, 17, { false }, { false }, isHighlightable = { !container.getItem(7).isEmpty }))
        }

        constructor(
            id: Int,
            inv: Inventory,
            buf: FriendlyByteBuf
        ): this(
            id,
            inv,
            SimpleContainer(8),
            be(inv.player.commandSenderWorld, buf.readBlockPos()),
            ContainerLevelAccess.NULL,
            SimpleContainerData(2)
        )

        override fun stillValid(player: Player): Boolean = stillValid(this.access, player, ECRegistry.magicTable)
    }
}