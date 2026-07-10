package com.algorithmlx.ecr.neoforge.init

import com.algorithmlx.ecr.api.item.SoulStoneLike
import com.algorithmlx.ecr.common.components.SoulStoneComponent
import com.algorithmlx.ecr.common.init.events.ECEvents
import com.algorithmlx.ecr.common.init.registry.DataComponentRegistry
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent

fun initForgeEvents(bus: IEventBus) {
    bus.addListener(::onItemTooltip)
    bus.addListener(::onItemPickup)
}

fun onItemTooltip(event: ItemTooltipEvent) {
    ECEvents.itemTooltip(event.itemStack, event.toolTip)
}

fun onItemPickup(event: ItemEntityPickupEvent.Post) {
    val original = event.originalStack
    val current = event.currentStack

    if (original.item !is SoulStoneLike) return

    if (original.has(DataComponentRegistry.instance.soulStone) && original.get(DataComponentRegistry.instance.soulStone) != SoulStoneComponent.EMPTY) return

    current.set(
        DataComponentRegistry.instance.soulStone,
        SoulStoneComponent(event.player.uuid, event.player.name.string, 0)
    )
}
