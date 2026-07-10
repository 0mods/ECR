package com.algorithmlx.ecr.client.book

import com.algorithmlx.ecr.api.ModId
import com.algorithmlx.ecr.api.client.research.BookRecipeRenderBuilder
import com.algorithmlx.ecr.api.client.research.BookRecipeRenderer
import com.algorithmlx.ecr.api.client.research.BookRecipeSlotType
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.crafting.CraftingRecipe
import net.minecraft.world.item.crafting.display.ShapedCraftingRecipeDisplay
import net.minecraft.world.item.crafting.display.ShapelessCraftingRecipeDisplay
import net.minecraft.world.item.crafting.display.SlotDisplay

object CraftingTableRecipeRenderer : BookRecipeRenderer<CraftingRecipe> {
    private val item by lazy { ItemStack(Items.CRAFTING_TABLE) }
    override fun build(recipe: CraftingRecipe, builder: BookRecipeRenderBuilder) {
        when (val display = recipe.display().firstOrNull { it is ShapedCraftingRecipeDisplay || it is ShapelessCraftingRecipeDisplay }) {
            is ShapedCraftingRecipeDisplay -> {
                renderGrid(builder) { row, column ->
                    if (row < display.height && column < display.width) {
                        display.ingredients.getOrNull(row * display.width + column)
                    } else {
                        null
                    }
                }

                renderResult(builder, display.result)
            }

            is ShapelessCraftingRecipeDisplay -> {
                renderGrid(builder) { row, column ->
                    display.ingredients.getOrNull(row * GRID_WIDTH + column)
                }

                renderResult(builder, display.result)
            }

            else -> Unit
        }
    }

    private fun renderGrid(builder: BookRecipeRenderBuilder, ingredientAt: (row: Int, column: Int) -> SlotDisplay?) {
        for (row in 0 until GRID_WIDTH) {
            for (column in 0 until GRID_WIDTH) {
                val x = column * SLOT_SIZE
                val y = row * SLOT_SIZE
                val ingredient = ingredientAt(row, column)
                if (ingredient == null) {
                    builder.slotAdd(emptyList(), BookRecipeSlotType.INPUT, x, y)
                } else {
                    builder.slotAdd(ingredient, BookRecipeSlotType.INPUT, x, y)
                }
            }
        }
    }

    private fun renderResult(builder: BookRecipeRenderBuilder, result: SlotDisplay) {
        builder.slotAdd(result, BookRecipeSlotType.RESULT, RESULT_X, RESULT_Y)
        builder.item(item, RESULT_X + ITEM_OFFSET + 4, CRAFTING_STATION_Y)
        builder.tooltip(
            Component.empty()
                .append(item.itemName)
                .append(" ")
                .append(Component.translatable("tooltip.$ModId.recipe")),
            RESULT_X + ITEM_OFFSET * 2,
            CRAFTING_STATION_Y,
            16
        )
    }

    private const val SLOT_SIZE = 32
    private const val GRID_WIDTH = 3
    private const val GRID_SIZE = SLOT_SIZE * GRID_WIDTH
    private const val RESULT_X = GRID_SIZE + SLOT_SIZE
    private const val RESULT_Y = (GRID_SIZE - SLOT_SIZE) / 2
    private const val ITEM_OFFSET = 4
    private const val CRAFTING_STATION_Y = RESULT_Y + SLOT_SIZE + ITEM_OFFSET
}
