package com.algorithmlx.ecr.common.init.events

import com.algorithmlx.ecr.api.ModId
import com.algorithmlx.ecr.common.components.SoulStoneComponent
import com.algorithmlx.ecr.common.init.registry.DataComponentRegistry
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack

object ECEvents {
    fun itemTooltip(item: ItemStack, tooltips: MutableList<Component>) {
        val component = item.getOrDefault(DataComponentRegistry.instance.soulStone, SoulStoneComponent.EMPTY)

        if (component == SoulStoneComponent.EMPTY) return

        tooltips += if (component.ownerName.isNotEmpty())
            Component.translatable(
                "tooltip.$ModId.soul_stone.tracking",
                Component.literal(component.ownerName).withStyle(ChatFormatting.GOLD)
            ).withStyle(ChatFormatting.DARK_GRAY)
        else Component.translatable("tooltip.$ModId.soul_stone.error").withStyle(ChatFormatting.DARK_RED)

        tooltips += Component.translatable(
            "tooltip.$ModId.soul_stone.detected_ubmru",
            Component.literal(component.capacity.toString()).withStyle(ChatFormatting.GREEN)
        ).withStyle(ChatFormatting.DARK_GRAY)
    }
}
