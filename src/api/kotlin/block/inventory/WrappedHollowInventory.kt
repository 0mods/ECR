package team._0mods.ecr.api.block.inventory

import net.minecraft.world.Container
import net.minecraft.world.item.ItemStack
import ru.hollowhorizon.hc.common.capabilities.CapabilityInstance
import ru.hollowhorizon.hc.common.capabilities.containers.HollowContainer

class WrappedHollowInventory(
    private val original: HollowContainer,
    capability: CapabilityInstance,
    private val extract: (Int) -> Boolean,
    canPlace: (Int, ItemStack) -> Boolean
) : HollowContainer(
    capability,
    original.size,
    canPlace
) {
    override fun canTakeItem(target: Container, index: Int, stack: ItemStack): Boolean {
        return if (extract(index)) original.canTakeItem(target, index, stack) else false
    }

    override fun canPlaceItem(slot: Int, stack: ItemStack): Boolean {
        return if (canPlace(slot, stack)) original.canPlaceItem(slot, stack) else false
    }
}