package team._0mods.ecr.api.block.inventory

import net.minecraft.world.item.ItemStack
import net.minecraftforge.items.IItemHandlerModifiable

class WrappedInventory(
    private val handler: IItemHandlerModifiable,
    private val extract: (Int) -> Boolean,
    private val insert: (Int, ItemStack) -> Boolean
): IItemHandlerModifiable {
    override fun setStackInSlot(slot: Int, stack: ItemStack) = this.handler.setStackInSlot(slot, stack)

    override fun getSlots(): Int = this.handler.slots

    override fun getStackInSlot(slot: Int): ItemStack = handler.getStackInSlot(slot)

    override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack =
        if (this.insert(slot, stack))
            this.handler.insertItem(slot, stack, simulate)
        else stack

    override fun extractItem(slot: Int, amount: Int, simulate: Boolean): ItemStack =
        if (this.extract(slot))
            this.handler.extractItem(slot, amount, simulate)
        else ItemStack.EMPTY

    override fun getSlotLimit(slot: Int): Int = this.handler.getSlotLimit(slot)

    override fun isItemValid(slot: Int, stack: ItemStack): Boolean = this.insert(slot, stack) && this.handler.isItemValid(slot, stack)
}