package com.algorithmlx.ecr.neoforge.init

import com.algorithmlx.ecr.api.ModId
import com.algorithmlx.ecr.common.components.SoulStoneComponent
import com.algorithmlx.ecr.common.init.registry.DataComponentRegistry
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent

fun initForgeEvents(bus: IEventBus) {
    bus.addListener(::onItemTooltip)
}

fun onItemTooltip(event: ItemTooltipEvent) {
    val item = event.itemStack
    val component = item.getOrDefault(DataComponentRegistry.instance.soulStone, SoulStoneComponent.EMPTY)

    if (component == SoulStoneComponent.EMPTY) return

    if (component.ownerName.isNotEmpty())
        event.toolTip += Component.translatable(
            "tooltip.$ModId.soul_stone.tracking",
            Component.literal(component.ownerName).withStyle(ChatFormatting.GOLD)
        ).withStyle(ChatFormatting.DARK_GRAY)
    else event.toolTip += Component.translatable("tooltip.$ModId.soul_stone.error").withStyle(ChatFormatting.DARK_RED)

    event.toolTip += Component.translatable(
        "tooltip.$ModId.soul_stone.detected_ubmru",
        Component.literal(component.capacity.toString()).withStyle(ChatFormatting.GREEN)
    ).withStyle(ChatFormatting.DARK_GRAY)
}
