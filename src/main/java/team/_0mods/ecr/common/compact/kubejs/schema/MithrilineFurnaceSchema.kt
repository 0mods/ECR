package team._0mods.ecr.common.compact.kubejs.schema

import dev.latvian.mods.kubejs.item.InputItem
import dev.latvian.mods.kubejs.item.OutputItem
import dev.latvian.mods.kubejs.recipe.RecipeKey
import dev.latvian.mods.kubejs.recipe.component.ItemComponents
import dev.latvian.mods.kubejs.recipe.component.NumberComponent
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema

interface MithrilineFurnaceSchema {
    companion object {
        @JvmField val INPUT: RecipeKey<InputItem> = ItemComponents.INPUT.key("ingredient").noBuilders()
        @JvmField val OUTPUT: RecipeKey<OutputItem> = ItemComponents.OUTPUT.key("result").noBuilders()
        @JvmField val ESPE: RecipeKey<Int> = NumberComponent.INT.key("espe").optional(100).alwaysWrite()

        @JvmField val SCHEMA: RecipeSchema = RecipeSchema(INPUT, OUTPUT, ESPE)
    }
}
