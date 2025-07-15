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
import team._0mods.ecr.common.init.registry.ECRRegistry

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
        addSlot(VanillaSpecialSlot(container, 0, 26, 17, stackSize = stackSize, place = { this.item.isEmpty }))
        addSlot(VanillaSpecialSlot(container, 1, 62, 17, stackSize = stackSize, place = { this.item.isEmpty }))
        addSlot(VanillaSpecialSlot(container, 2, 26, 53, stackSize = stackSize, place = { this.item.isEmpty }))
        addSlot(VanillaSpecialSlot(container, 3, 62, 53, stackSize = stackSize, place = { this.item.isEmpty }))

        // Catalyst
        addSlot(VanillaSpecialSlot(container, 4, 44, 35, stackSize = stackSize, stackSizeWithItem = { stackSize }))
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

    class Envoyer(
        containerId: Int,
        inv: Inventory,
        container: Container,
        blockEntity: XLikeBlockEntity?,
        access: ContainerLevelAccess,
        data: ContainerData
    ): XLikeMenu(ECRRegistry.envoyerMenu, containerId, inv, container, blockEntity, access, data) {
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

        override fun moveItemStackTo(stack: ItemStack, startIndex: Int, endIndex: Int, reverse: Boolean): Boolean {
            var moved = false
            var index = if (reverse) endIndex - 1 else startIndex

            while (!stack.isEmpty && (if (reverse) index >= startIndex else index < endIndex)) {
                val slot = this.slots[index]
                val existing = slot.item

                if (!existing.isEmpty && ItemStack.isSameItemSameTags(stack, existing)) {
                    val maxStackSize = minOf(stack.maxStackSize, slot.maxStackSize)
                    val transferable = maxStackSize - existing.count

                    if (transferable > 0) {
                        val toMove = minOf(stack.count, transferable)
                        existing.grow(toMove)
                        stack.shrink(toMove)
                        slot.setChanged()
                        moved = true
                    }
                }

                index += if (reverse) -1 else 1
            }

            index = if (reverse) endIndex - 1 else startIndex
            while (!stack.isEmpty && (if (reverse) index >= startIndex else index < endIndex)) {
                val slot = this.slots[index]
                val existing = slot.item

                if (existing.isEmpty && slot.mayPlace(stack)) {
                    val toMove = minOf(stack.count, slot.maxStackSize)
                    val newStack = stack.copy()
                    newStack.count = toMove
                    slot.set(newStack)
                    stack.shrink(toMove)
                    slot.setChanged()
                    moved = true
                }

                index += if (reverse) -1 else 1
            }

            return moved
        }

        override fun quickMoveStack(player: Player, index: Int): ItemStack {
            val sourceSlot = this.slots[index]
            if (!sourceSlot.hasItem()) return ItemStack.EMPTY

            val originalStack = sourceSlot.item
            if (originalStack.isEmpty) return ItemStack.EMPTY

            val extracted = sourceSlot.remove(1)
            if (extracted.isEmpty) return ItemStack.EMPTY

            val moved = when (index) {
                0 -> this.moveItemStackTo(extracted, 7, 43, true)
                in 7 ..< 43 -> {
                    if (!this.moveItemStackTo(extracted, 0, 6, false)) {
                        if (index in 17 ..< 43)
                            this.moveItemStackTo(extracted, 7, 17, false)
                        else
                            this.moveItemStackTo(extracted, 17, 43, false)
                    } else true
                }
                else -> this.moveItemStackTo(extracted, 7, 43, false)
            }

            if (!moved) {
                sourceSlot.set(originalStack)
                sourceSlot.setChanged()
                return ItemStack.EMPTY
            }

            sourceSlot.setChanged()
            sourceSlot.onTake(player, extracted)
            return extracted
        }

        override fun stillValid(player: Player): Boolean = stillValid(this.access, player, ECRRegistry.envoyer)
    }

    class MagicTable(
        containerId: Int,
        inv: Inventory,
        container: Container,
        blockEntity: XLikeBlockEntity?,
        access: ContainerLevelAccess,
        data: ContainerData
    ): XLikeMenu(ECRRegistry.magicTableMenu, containerId, inv, container, blockEntity, access, data) {
        init {
            buildSlots(container, inv)

            addSlot(VanillaSpecialSlot(container, 7, 152, 16, { false }, { false }, isHighlightable = { !container.getItem(7).isEmpty }))
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

        override fun quickMoveStack(player: Player, index: Int): ItemStack {
            val slot = this.slots.getOrNull(index) ?: return ItemStack.EMPTY
            val stack = slot.item.takeIf { it.count > 0 } ?: return ItemStack.EMPTY

            val copy = stack.copy()
            val moved = when (index) {
                0 -> moveItemStackTo(stack, 7, 43, true)
                in 7 ..< 17 -> moveItemStackTo(stack, 0, 6, false) || moveItemStackTo(stack, 17, 43, false)
                in 17 ..< 43 -> moveItemStackTo(stack, 0, 6, false) || moveItemStackTo(stack, 7, 16, false)
                else -> moveItemStackTo(stack, 7, 43, false)
            }

            if (!moved) return ItemStack.EMPTY

            if (stack.isEmpty) slot.set(ItemStack.EMPTY)
            else slot.setChanged()

            slot.onTake(player, stack)
            return if (stack.count == copy.count) ItemStack.EMPTY else copy
        }

        override fun stillValid(player: Player): Boolean = stillValid(this.access, player, ECRRegistry.magicTable)
    }
}
