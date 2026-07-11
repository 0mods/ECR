package com.algorithmlx.ecr.common.item

import com.algorithmlx.ecr.api.registries.ECRegistries
import com.algorithmlx.ecr.api.research.BookType
import com.algorithmlx.ecr.api.research.ResearchProgress
import com.algorithmlx.ecr.common.init.registry.BookLevelRegistry
import com.algorithmlx.ecr.common.init.registry.DataComponentRegistry
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.level.Level

class ResearchBookItem(properties: Properties) : Item(properties) {
    override fun use(level: Level, player: Player, hand: InteractionHand): InteractionResult {
        val stack = player.getItemInHand(hand)
        val bookType = stack.getOrDefault(DataComponentRegistry.instance.bookType, BookLevelRegistry.instance.basic)
        if (level.isClientSide) {
            ResearchBookHooks.open(bookType)
        } else if (player is ServerPlayer) {
            bookTypeId(bookType)?.let { ResearchProgress.setBookLevel(player, it) }
        }
        return InteractionResult.SUCCESS
    }
}

private fun bookTypeId(bookType: BookType) = ECRegistries.BOOK_TYPES.getKey(bookType)
    ?: ECRegistries.BOOK_TYPES.entrySet().firstOrNull { it.value == bookType }?.key?.identifier()

object ResearchBookHooks {
    @JvmField var open: (BookType) -> Unit = {}
}
