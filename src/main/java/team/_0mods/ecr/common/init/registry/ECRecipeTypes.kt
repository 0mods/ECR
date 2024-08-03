package team._0mods.ecr.common.init.registry

import net.minecraft.world.item.crafting.RecipeType
import team._0mods.ecr.ModId
import team._0mods.ecr.common.recipes.MithrilineFurnaceRecipe

object ECRecipeTypes {
    val mithrilineFurnace: RecipeType<MithrilineFurnaceRecipe> = RecipeType.register("$ModId:mithriline_furnace")
}