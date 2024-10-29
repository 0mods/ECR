package team._0mods.ecr.common.compact.ct

import com.blamejared.crafttweaker.api.CraftTweakerAPI
import com.blamejared.crafttweaker.api.CraftTweakerConstants
import com.blamejared.crafttweaker.api.action.recipe.ActionAddRecipe
import com.blamejared.crafttweaker.api.action.recipe.ActionRemoveRecipeByName
import com.blamejared.crafttweaker.api.annotation.ZenRegister
import com.blamejared.crafttweaker.api.ingredient.IIngredient
import com.blamejared.crafttweaker.api.item.IItemStack
import com.blamejared.crafttweaker.api.recipe.manager.base.IRecipeManager
import net.minecraft.core.NonNullList
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.item.crafting.RecipeType
import org.openzen.zencode.java.ZenCodeType
import ru.hollowhorizon.hc.client.utils.rl
import team._0mods.ecr.api.SHORT_ID
import team._0mods.ecr.common.init.registry.ECRegistry
import team._0mods.ecr.common.recipes.EnvoyerRecipe

@ZenRegister
@ZenCodeType.Name("mods.$SHORT_ID.Envoyer")
object EnvoyerCTRecipe: IRecipeManager<EnvoyerRecipe> {
    override fun getRecipeType(): RecipeType<EnvoyerRecipe> = ECRegistry.envoyerRecipe.get()

    @JvmStatic
    @ZenCodeType.Method
    fun addRecipe(
        name: String,
        output: IItemStack,
        catalyst: IIngredient,
        @ZenCodeType.OptionalInt(100) time: Int,
        @ZenCodeType.OptionalInt(10) mru: Int
    ) = addRecipe(name, output, catalyst, arrayOf(), time, mru)

    @JvmStatic
    @ZenCodeType.Method
    fun addRecipe(
        name: String,
        output: IItemStack,
        catalyst: IIngredient,
        ingredients: Array<IIngredient>,
        @ZenCodeType.OptionalInt(100) time: Int,
        @ZenCodeType.OptionalInt(10) mru: Int
    ) {
        val fixedName = fixRecipeName(name)
        val inputs = NonNullList.withSize(4, Ingredient.EMPTY)
        val catal = NonNullList.withSize(1, Ingredient.EMPTY)
        catal[0] = catalyst.asVanillaIngredient()

        (0 ..< ingredients.size).forEach { i ->
            inputs[i] = ingredients[i].asVanillaIngredient()
        }

        CraftTweakerAPI.apply(
            ActionAddRecipe(
                EnvoyerCTRecipe,
                EnvoyerRecipe(CraftTweakerConstants.rl(fixedName), inputs, catal, time, mru, output.internal)
            )
        )
    }

    @JvmStatic
    @ZenCodeType.Method
    fun removeById(id: String) {
        CraftTweakerAPI.apply(ActionRemoveRecipeByName(EnvoyerCTRecipe, id.rl))
    }
}
