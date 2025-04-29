package team._0mods.ecr.common.compact.rei.display

import me.shedaniel.rei.api.common.category.CategoryIdentifier
import me.shedaniel.rei.api.common.display.basic.BasicDisplay
import me.shedaniel.rei.api.common.entry.EntryIngredient
import me.shedaniel.rei.api.common.registry.RecipeManagerContext
import me.shedaniel.rei.api.common.util.EntryIngredients
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.item.crafting.Recipe
import team._0mods.ecr.common.compact.rei.ENVOYER
import team._0mods.ecr.common.recipes.XLikeRecipe
import java.util.Optional

class EnvoyerDisplay(
    input: List<EntryIngredient>,
    output: List<EntryIngredient>,
    private val recipe: Recipe<*>,
    val time: Int,
    val mru: Int,
): BasicDisplay(input, output, Optional.ofNullable(recipe).map { it.id }) {
    constructor(
        input: List<EntryIngredient>,
        output: List<EntryIngredient>,
        tag: CompoundTag
    ): this(
        input,
        output,
        RecipeManagerContext.getInstance().byId(tag, "location"),
        tag.getInt("time"),
        tag.getInt("mru")
    )

    constructor(recipe: XLikeRecipe.Envoyer): this(
        EntryIngredients.ofIngredients(recipe.ingredients),
        listOf(
            EntryIngredients.of(recipe.getResultItem(registryAccess()) )
        ),
        recipe,
        recipe.time,
        recipe.mruPerTick
    )

    override fun getCategoryIdentifier(): CategoryIdentifier<*>? = ENVOYER

    companion object {
        @JvmStatic
        fun serializer(constructor: Serializer.RecipeLessConstructor<EnvoyerDisplay>): BasicDisplay.Serializer<EnvoyerDisplay> =
            Serializer.ofRecipeLess(constructor) { display, tag ->
                tag.putInt("time", display.time)
                tag.putInt("mru", display.mru)
            }
    }
}