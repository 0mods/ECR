package team._0mods.ecr.common.utils.recipe

import net.minecraft.core.NonNullList
import net.minecraft.world.Container
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.level.Level
import net.minecraftforge.common.util.RecipeMatcher
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.wrapper.InvWrapper

interface ForgefiedRecipe: Recipe<Container> {
    override fun assemble(container: Container): ItemStack = this.assemble(InvWrapper(container))

    override fun matches(container: Container, level: Level): Boolean = this.matches(InvWrapper(container))

    override fun getRemainingItems(container: Container): NonNullList<ItemStack> {
        return this.getRemainingItems(InvWrapper(container))
    }

    fun assemble(inv: IItemHandler): ItemStack

    fun matches(inv: IItemHandler) = this.matches(inv, 0, inv.slots)

    fun matches(inv: IItemHandler, startIndex: Int, endIndex: Int): Boolean {
        val `in` = NonNullList.create<ItemStack>()
        for (i in startIndex ..< endIndex) {
            `in` += inv.getStackInSlot(i)
        }

        return RecipeMatcher.findMatches(`in`, this.ingredients) != null
    }

    fun getRemainingItems(inv: IItemHandler): NonNullList<ItemStack> {
        val rem = NonNullList.withSize(inv.slots, ItemStack.EMPTY)

        for (i in 0 ..< rem.size) {
            val s = inv.getStackInSlot(i)

            if (s.hasCraftingRemainingItem())
                rem[i] = s.craftingRemainingItem
        }

        return rem
    }
}
