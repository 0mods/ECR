package com.algorithmlx.ecr.registry

import com.algorithmlx.ecr.api.ModId
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister

@Suppress("ACTUAL_WITHOUT_EXPECT", "unused")
actual object CreativeTabRegistry {
    private val creativeTabs = DeferredRegister.create(BuiltInRegistries.CREATIVE_MODE_TAB, ModId)

    fun init(bus: IEventBus) {
        creativeTabs.register(bus)
    }

    private val itemsTab = creativeTabs.register("tab_items") { _ ->
        CreativeModeTab.builder()
            .icon { ItemStack(ItemRegistry.elementalGem) }
            .build()
    }

    private val blocksTab = creativeTabs.register("tab_blocks") { _ ->
        CreativeModeTab.builder()
            .icon { ItemStack(BlockRegistry.mithrilineFurnace) }
            .withTabsBefore(itemsTab.key)
            .build()
    }

    actual val items: CreativeModeTab by lazy { itemsTab.get() }
    actual val blocks: CreativeModeTab by lazy { blocksTab.get() }
}
