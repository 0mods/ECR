package com.algorithmlx.ecr.api.item

interface SoulStoneLike: HasSubItem {
    val receiveCount: Int

    val extractCount: Int

    /*override fun addSubItems(original: ItemStack): List<ItemStack> = listOf(original, original.copy().apply { this.isCreative = true })*/
}
