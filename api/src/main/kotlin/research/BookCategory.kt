package com.algorithmlx.ecr.api.research

import kotlinx.serialization.Serializable
import net.minecraft.resources.Identifier
import net.minecraft.world.item.ItemStack

@Serializable
sealed interface BookCategory {
    /**
     * Binds the current category to the one listed here
     * @return [BookCategory] to which the `this` category will be bound
     */
    val previousCategory: BookCategory?

    val icon: BookCategoryIcon

    val includedCategories: List<BookCategory>

    val pages: List<BookEntry>

    val bookLevel: BookLevel
}

class BookCategoryIcon(val item: ItemStack?, val texture: Identifier?) {
    companion object {
        @JvmStatic
        fun item(item: ItemStack) = BookCategoryIcon(item, null)

        @JvmStatic
        fun texture(texture: Identifier) = BookCategoryIcon(null, texture)
    }
}
