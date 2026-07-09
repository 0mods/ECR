package com.algorithmlx.ecr.neoforge.init

import com.algorithmlx.ecr.common.init.events.ECEvents
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent

fun initForgeEvents(bus: IEventBus) {
    bus.addListener(::onItemTooltip)
}

fun onItemTooltip(event: ItemTooltipEvent) {
    ECEvents.itemTooltip(event.itemStack, event.toolTip)
}
