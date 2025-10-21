package team._0mods.ecr.common.compact.rei.display

import me.shedaniel.rei.api.common.category.CategoryIdentifier
import me.shedaniel.rei.api.common.display.basic.BasicDisplay
import me.shedaniel.rei.api.common.entry.EntryIngredient
import me.shedaniel.rei.api.common.registry.RecipeManagerContext
import me.shedaniel.rei.api.common.util.EntryIngredients
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.item.crafting.Recipe
import ru.hollowhorizon.hc.common.utils.rl
import team._0mods.ecr.common.compact.rei.ENVOYER_DISPLAY
import team._0mods.ecr.common.compact.rei.MAGIC_TABLE_DISPLAY
import team._0mods.ecr.common.recipes.XLikeRecipe
import java.util.Optional

abstract class XLikeDisplay(
    input: List<EntryIngredient>,
    output: List<EntryIngredient>,
    recipe: Recipe<*>,
    val time: Int,
    val mru: Int
): BasicDisplay(input, output, Optional.ofNullable(recipe).map { it.id }) {
    class Envoyer(
        input: List<EntryIngredient>,
        output: List<EntryIngredient>,
        recipe: Recipe<*>,
        time: Int,
        mru: Int
    ): XLikeDisplay(input, output, recipe, time, mru) {
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
            listOf(EntryIngredients.of(recipe.getResultItem(registryAccess()))),
            recipe,
            recipe.time,
            recipe.mruPerTick
        )

        override fun getCategoryIdentifier(): CategoryIdentifier<*> = ENVOYER_DISPLAY
    }

    class MagicTable(
        input: List<EntryIngredient>, output: List<EntryIngredient>, recipe: Recipe<*>, time: Int, mru: Int
    ): XLikeDisplay(input, output, recipe, time, mru) {
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

        constructor(recipe: XLikeRecipe.MagicTable): this(
            EntryIngredients.ofIngredients(recipe.ingredients),
            listOf(EntryIngredients.of(recipe.getResultItem(registryAccess()) )),
            recipe,
            recipe.time,
            recipe.mruPerTick
        )

        override fun getCategoryIdentifier(): CategoryIdentifier<*> = MAGIC_TABLE_DISPLAY
    }

    companion object {
        fun <T: XLikeDisplay> serializer(constructor: Serializer.RecipeLessConstructor<T>): Serializer<T> =
            Serializer.ofRecipeLess(constructor) { display, tag ->
                tag.putInt("time", display.time)
                tag.putInt("mru", display.mru)
            }
    }
}
