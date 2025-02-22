package team._0mods.ecr.api.container.slot

import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.SlotItemHandler

class SpecialSlot(
    itemHandler: IItemHandler?,
    index: Int,
    xPosition: Int,
    yPosition: Int,
    private val place: (SpecialSlot.(ItemStack) -> Boolean)? = null,
    private val pickup: (SpecialSlot.(Player) -> Boolean)? = null,
    private val stackSize: Int? = null,
    private val stackSizeItem: ((ItemStack) -> Int)? = null
) : SlotItemHandler(
    itemHandler,
    index,
    xPosition,
    yPosition
) {
    override fun getMaxStackSize(): Int = stackSize ?: super.getMaxStackSize()

    override fun getMaxStackSize(stack: ItemStack): Int = stackSizeItem?.let { it(stack) } ?: super.getMaxStackSize(stack)

    override fun mayPickup(player: Player): Boolean = pickup?.let { it(player) } ?: super.mayPickup(player)

    override fun mayPlace(stack: ItemStack): Boolean = place?.let { it(stack) } ?: super.mayPlace(stack)
}
