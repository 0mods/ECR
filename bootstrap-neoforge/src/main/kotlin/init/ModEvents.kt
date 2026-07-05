package com.algorithmlx.ecr.neoforge.init

import com.algorithmlx.ecr.api.registries.ECRegistries
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.NewRegistryEvent

fun initEvents(bus: IEventBus) {
    bus.addListener(::onNewRegistry)
}

fun onNewRegistry(event: NewRegistryEvent) {
    event.register(ECRegistries.MULTIBLOCK)
    event.register(ECRegistries.MRU_TYPE)
    event.register(ECRegistries.BOOK_LEVEL)
}
