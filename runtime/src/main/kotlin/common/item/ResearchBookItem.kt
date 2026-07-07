package com.algorithmlx.ecr.common.item

import com.algorithmlx.ecr.common.init.registry.BookLevelRegistry
import com.algorithmlx.ecr.common.init.registry.DataComponentRegistry
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.level.Level

class ResearchBookItem(properties: Properties) : Item(
    properties.component(
        DataComponentRegistry.instance.bookType,
        BookLevelRegistry.instance.basic
    )
) {
    override fun use(level: Level, player: Player, hand: InteractionHand): InteractionResult {
        if (level.isClientSide) ResearchBookHooks.open()
        return InteractionResult.SUCCESS
    }
}

object ResearchBookHooks {
    @JvmField var open: () -> Unit = {}
}
