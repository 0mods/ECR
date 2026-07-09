package com.algorithmlx.ecr.neoforge.init

import com.algorithmlx.ecr.api.ModId
import com.algorithmlx.ecr.api.item.HasSubItem
import com.algorithmlx.ecr.api.item.NoTab
import com.algorithmlx.ecr.api.registries.ECRegistries
import com.algorithmlx.ecr.common.init.registry.CreativeTabRegistry
import com.algorithmlx.ecr.common.item.NamedBlockItem
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.ItemStack
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent
import net.neoforged.neoforge.registries.NewRegistryEvent

fun initEvents(bus: IEventBus) {
    bus.addListener(::onNewRegistry)
    bus.addListener(::onCreativeTabs)
}

fun onNewRegistry(event: NewRegistryEvent) {
    event.register(ECRegistries.MULTIBLOCK)
    event.register(ECRegistries.MRU_TYPE)
    event.register(ECRegistries.BOOK_TYPES)
    event.register(ECRegistries.BOOK_ELEMENT_SERIALIZER)
    event.register(ECRegistries.RESEARCH_TASK_SERIALIZER)
    event.register(ECRegistries.MULTIBLOCK_MATCHER_TYPE)
}

fun onCreativeTabs(event: BuildCreativeModeTabContentsEvent) {
    BuiltInRegistries.ITEM.keySet().filter { it.namespace == ModId }.forEach {
        val item = BuiltInRegistries.ITEM.getOptional(it).get()
        if (event.tab == CreativeTabRegistry.instance.blocks) {
            if ((item is BlockItem || item is NamedBlockItem)  && item.block !is NoTab)
                event.accept(item)
            return@forEach
        }

        if (item is NoTab || event.tab != CreativeTabRegistry.instance.items) return@forEach

        if (item is HasSubItem) {
            item.addSubItems(ItemStack(item)).forEach { stack ->
                event.accept(stack)
            }

            return@forEach
        }

        event.accept(item)
    }
}
