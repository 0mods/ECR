package team._0mods.ecr.api.container

import net.minecraft.world.Container
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ContainerLevelAccess
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.inventory.Slot

abstract class AbstractContainer(
    menuType: MenuType<*>?,
    containerId: Int,
    val access: ContainerLevelAccess
) : AbstractContainerMenu(menuType, containerId) {
    private fun addSlotRange(playerInventory: Container, index: Int, x: Int, y: Int, amount: Int, dx: Int): Int {
        var index0 = index
        var x0 = x
        for (i in 0 ..< amount) {
            addSlot(Slot(playerInventory, index0, x0, y))
            x0 += dx
            index0++
        }
        return index0
    }

    private fun addSlotBox(
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

    protected fun makeInv(playerInventory: Container, leftCol: Int, topRow: Int) {
        var topRow0 = topRow
        addSlotBox(playerInventory, 9, leftCol, topRow0, 9, 18, 3, 18)

        topRow0 += 58
        addSlotRange(playerInventory, 0, leftCol, topRow0, 9, 18)
    }
}