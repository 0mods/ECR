package com.algorithmlx.ecr.neoforge.init

import com.algorithmlx.ecr.api.registries.ECRegistries
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent
import net.neoforged.neoforge.registries.NewRegistryEvent

fun initEvents(bus: IEventBus) {
    bus.addListener(::onNewRegistry)
}

fun onNewRegistry(event: NewRegistryEvent) {
    event.register(ECRegistries.MULTIBLOCK)
    event.register(ECRegistries.MRU_TYPE)
    event.register(ECRegistries.BOOK_TYPES)
    event.register(ECRegistries.BOOK_ELEMENT_SERIALIZER)
    event.register(ECRegistries.RESEARCH_TASK_SERIALIZER)
    event.register(ECRegistries.MULTIBLOCK_MATCHER_TYPE)
}
