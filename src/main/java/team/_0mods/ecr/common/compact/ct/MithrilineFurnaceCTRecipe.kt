package team._0mods.ecr.common.compact.ct

import com.blamejared.crafttweaker.api.CraftTweakerAPI
import com.blamejared.crafttweaker.api.CraftTweakerConstants
import com.blamejared.crafttweaker.api.action.recipe.ActionAddRecipe
import com.blamejared.crafttweaker.api.action.recipe.ActionRemoveRecipeByName
import com.blamejared.crafttweaker.api.action.recipe.ActionRemoveRecipeByOutput
import com.blamejared.crafttweaker.api.action.recipe.ActionRemoveRecipeByOutputInput
import com.blamejared.crafttweaker.api.annotation.ZenRegister
import com.blamejared.crafttweaker.api.ingredient.IIngredient
import com.blamejared.crafttweaker.api.item.IItemStack
import com.blamejared.crafttweaker.api.recipe.manager.base.IRecipeManager
import net.minecraft.core.NonNullList
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.item.crafting.RecipeType
import org.openzen.zencode.java.ZenCodeType
import ru.hollowhorizon.hc.common.utils.rl
import team._0mods.ecr.api.SHORT_ID
import team._0mods.ecr.common.init.registry.ECRegistry
import team._0mods.ecr.common.recipes.MithrilineFurnaceRecipe

@ZenRegister
@ZenCodeType.Name("mods.${SHORT_ID}.MithrilineFurnace")
object MithrilineFurnaceCTRecipe: IRecipeManager<MithrilineFurnaceRecipe> {
    override fun getRecipeType(): RecipeType<MithrilineFurnaceRecipe> = ECRegistry.mithrilineFurnaceRecipe

    @JvmStatic
    @ZenCodeType.Method
    fun addRecipe(name: String, output: IItemStack, input: IIngredient, espe: Int) {
        val fixedName = fixRecipeName(name)
        val nnl = NonNullList.withSize(1, Ingredient.EMPTY)
        nnl[0] = input.asVanillaIngredient()
        CraftTweakerAPI.apply(ActionAddRecipe(MithrilineFurnaceCTRecipe, MithrilineFurnaceRecipe(CraftTweakerConstants.rl(fixedName), nnl, espe, output.internal)))
    }

    @JvmStatic
    @ZenCodeType.Method
    fun removeByOutput(output: IIngredient) {
        CraftTweakerAPI.apply(ActionRemoveRecipeByOutput(MithrilineFurnaceCTRecipe, output))
    }

    @JvmStatic
    @ZenCodeType.Method
    fun removeByIO(output: IItemStack, input: IIngredient) {
        CraftTweakerAPI.apply(ActionRemoveRecipeByOutputInput(MithrilineFurnaceCTRecipe, output, input))
    }

    @JvmStatic
    @ZenCodeType.Method
    fun removeById(id: String) {
        CraftTweakerAPI.apply(ActionRemoveRecipeByName(MithrilineFurnaceCTRecipe, id.rl))
    }
}