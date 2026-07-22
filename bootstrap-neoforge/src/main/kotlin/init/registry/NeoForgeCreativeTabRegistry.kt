package com.algorithmlx.ecr.neoforge.init.registry

import com.algorithmlx.ecr.api.ModId
import com.algorithmlx.ecr.registry.BlockRegistry
import com.algorithmlx.ecr.registry.CreativeTabRegistry
import com.algorithmlx.ecr.registry.ItemRegistry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister

object NeoForgeCreativeTabRegistry : CreativeTabRegistry {
    private val creativeTabs = DeferredRegister.create(BuiltInRegistries.CREATIVE_MODE_TAB, ModId)

    fun init(bus: IEventBus) {
        creativeTabs.register(bus)
    }

    private val itemsTab = creativeTabs.register("tab_items") { _ ->
        CreativeModeTab.builder()
            .icon { ItemStack(ItemRegistry.instance.elementalGem) }
            .build()
    }

    private val blocksTab = creativeTabs.register("tab_blocks") { _ ->
        CreativeModeTab.builder()
            .icon { ItemStack(BlockRegistry.instance.mithrilineFurnace) }
            .withTabsBefore(itemsTab.key)
            .build()
    }

    override val items: CreativeModeTab by lazy { itemsTab.get() }
    override val blocks: CreativeModeTab by lazy { blocksTab.get() }
}
