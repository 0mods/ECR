package team._0mods.ecr.api.container

import net.minecraft.world.Container
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ContainerLevelAccess
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack

abstract class AbstractMenu(
    menuType: MenuType<*>?,
    containerId: Int,
    val access: ContainerLevelAccess
) : AbstractContainerMenu(menuType, containerId) {
    protected fun addSlotRange(playerInventory: Container, index: Int, x: Int, y: Int, amount: Int, dx: Int): Int {
        var index0 = index
        var x0 = x
        for (i in 0 ..< amount) {
            addSlot(Slot(playerInventory, index0, x0, y))
            x0 += dx
            index0++
        }
        return index0
    }

    protected fun addSlotBox(
        playerInventory: Container,
        index: Int,
        x: Int,
        y: Int,
        horAmount: Int,
        dx: Int,
        verAmount: Int,
        dy: Int
    ): Int {
        var index0 = index
        var y0 = y
        for (j in 0 ..< verAmount) {
            index0 = addSlotRange(playerInventory, index0, x, y0, horAmount, dx)
            y0 += dy
        }
        return index0
    }

    @JvmName("makePlayerInventory")
    protected fun Container.make(leftCol: Int = 8, topRow: Int = 84) {
        var topRow0 = topRow
        addSlotBox(this, 9, leftCol, topRow0, 9, 18, 3, 18)

        topRow0 += 58
        addSlotRange(this, 0, leftCol, topRow0, 9, 18)
    }
}