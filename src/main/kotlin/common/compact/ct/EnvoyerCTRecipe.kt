package team._0mods.ecr.common.compact.ct

import com.blamejared.crafttweaker.api.CraftTweakerAPI
import com.blamejared.crafttweaker.api.CraftTweakerConstants
import com.blamejared.crafttweaker.api.action.recipe.ActionAddRecipe
import com.blamejared.crafttweaker.api.action.recipe.ActionRemoveRecipeByName
import com.blamejared.crafttweaker.api.annotation.ZenRegister
import com.blamejared.crafttweaker.api.ingredient.IIngredient
import com.blamejared.crafttweaker.api.ingredient.type.IIngredientEmpty
import com.blamejared.crafttweaker.api.item.IItemStack
import com.blamejared.crafttweaker.api.recipe.manager.base.IRecipeManager
import net.minecraft.core.NonNullList
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.item.crafting.RecipeType
import org.openzen.zencode.java.ZenCodeType
import ru.hollowhorizon.hc.common.utils.rl
import team._0mods.ecr.api.SHORT_ID
import team._0mods.ecr.common.init.registry.ECRRegistry
import team._0mods.ecr.common.recipes.XLikeRecipe
import java.util.UUID

@ZenRegister
@ZenCodeType.Name("mods.$SHORT_ID.Envoyer")
object EnvoyerCTRecipe: IRecipeManager<XLikeRecipe.Envoyer> {
    override fun getRecipeType(): RecipeType<XLikeRecipe.Envoyer> = ECRRegistry.envoyerRecipe

    @JvmStatic
    @ZenCodeType.Method
    fun addRecipe(
        output: IItemStack,
        ingredients: Array<IIngredient>,
        @ZenCodeType.OptionalInt(100) time: Int,
        @ZenCodeType.OptionalInt(10) mru: Int
    ) = addRecipe(output, IIngredientEmpty.INSTANCE, ingredients, time, mru)

    @JvmStatic
    @ZenCodeType.Method
    fun addRecipe(
        id: String,
        output: IItemStack,
        ingredients: Array<IIngredient>,
        @ZenCodeType.OptionalInt(100) time: Int,
        @ZenCodeType.OptionalInt(10) mru: Int
    ) = addRecipe(id, output, IIngredientEmpty.INSTANCE, ingredients, time, mru)

    @JvmStatic
    @ZenCodeType.Method
    fun addRecipe(
        output: IItemStack,
        catalyst: IIngredient,
        @ZenCodeType.OptionalInt(100) time: Int,
        @ZenCodeType.OptionalInt(10) mru: Int
    ) = addRecipe(output, catalyst, arrayOf(), time, mru)

    @JvmStatic
    @ZenCodeType.Method
    fun addRecipe(
        output: IItemStack,
        catalyst: IIngredient,
        ingredients: Array<IIngredient>,
        @ZenCodeType.OptionalInt(100) time: Int,
        @ZenCodeType.OptionalInt(10) mru: Int
    ) = addRecipe(UUID.randomUUID().toString(), output, catalyst, ingredients, time, mru)

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

        ingredients.indices.forEach { i ->
            inputs[i] = ingredients[i].asVanillaIngredient()
        }

        CraftTweakerAPI.apply(
            ActionAddRecipe(
                EnvoyerCTRecipe,
                XLikeRecipe.Envoyer(CraftTweakerConstants.rl(fixedName), inputs, catal, time, mru, output.internal)
            )
        )
    }

    @JvmStatic
    @ZenCodeType.Method
    fun removeById(id: String) {
        CraftTweakerAPI.apply(ActionRemoveRecipeByName(EnvoyerCTRecipe, id.rl))
    }
}
