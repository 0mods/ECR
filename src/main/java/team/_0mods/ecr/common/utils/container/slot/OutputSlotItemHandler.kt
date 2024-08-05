package team._0mods.ecr.common.utils.container.slot

import net.minecraft.world.item.ItemStack
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.SlotItemHandler

class OutputSlotItemHandler(
    itemHandler: IItemHandler?,
    index: Int,
    xPosition: Int,
    yPosition: Int
) : SlotItemHandler(itemHandler, index, xPosition, yPosition) {
    override fun mayPlace(stack: ItemStack): Boolean = false
}