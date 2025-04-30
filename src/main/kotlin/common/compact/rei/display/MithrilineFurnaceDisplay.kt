package team._0mods.ecr.common.compact.rei.display

import me.shedaniel.rei.api.common.category.CategoryIdentifier
import me.shedaniel.rei.api.common.display.basic.BasicDisplay
import me.shedaniel.rei.api.common.entry.EntryIngredient
import me.shedaniel.rei.api.common.registry.RecipeManagerContext
import me.shedaniel.rei.api.common.util.EntryIngredients
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.item.crafting.Recipe
import team._0mods.ecr.common.compact.rei.MITHRILINE_FURNACE_DISPLAY
import team._0mods.ecr.common.recipes.MithrilineFurnaceRecipe
import java.util.Optional

class MithrilineFurnaceDisplay(
    input: List<EntryIngredient>,
    output: List<EntryIngredient>,
    recipe: Recipe<*>,
    espe: Int
): BasicDisplay(input, output, Optional.ofNullable(recipe).map { it.id }) {
    constructor(
        input: List<EntryIngredient>,
        output: List<EntryIngredient>,
        tag: CompoundTag
    ): this(
        input,
        output,
        RecipeManagerContext.getInstance().byId(tag, "location"),
        tag.getInt("espe")
    )

    constructor(recipe: MithrilineFurnaceRecipe): this(
        EntryIngredients.ofIngredients(recipe.ingredients),
        listOf(EntryIngredients.of(recipe.result)),
        recipe,
        recipe.espe
    )

    override fun getCategoryIdentifier(): CategoryIdentifier<*> = MITHRILINE_FURNACE_DISPLAY
}
