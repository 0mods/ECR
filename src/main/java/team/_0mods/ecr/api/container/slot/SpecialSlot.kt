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
    private val place: SpecialSlot.(ItemStack) -> Boolean = { true },
    private val pickup: SpecialSlot.(Player) -> Boolean = { !this.itemHandler.extractItem(index, 1, true).isEmpty }
) : SlotItemHandler(
    itemHandler,
    index,
    xPosition,
    yPosition
) {
    override fun mayPickup(player: Player): Boolean {
        return pickup(player)
    }

    override fun mayPlace(stack: ItemStack): Boolean {
        return place(stack)
    }
}
