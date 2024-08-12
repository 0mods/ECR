package team._0mods.ecr.common.init.registry

import net.minecraft.core.NonNullList
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.crafting.Ingredient
import team._0mods.ecr.ModId
import team._0mods.ecr.api.utils.rl
import team._0mods.ecr.common.recipes.MithrilineFurnaceRecipe

object ECRecipes {
    fun init() {
        MithrilineFurnaceRecipe(
            "$ModId:test_recipe".rl,
            NonNullList.withSize(1, Ingredient.of(ItemStack(Items.COAL))),
            1,
            ItemStack(Items.DIAMOND)
        )

        ECRegistry.mithrilineFurnaceRecipe.get()
    }
}