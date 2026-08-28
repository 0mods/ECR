package com.algorithmlx.ecr.neoforge.init.registry

import com.algorithmlx.ecr.api.ModId
import com.algorithmlx.ecr.api.utils.ecRL
import com.algorithmlx.ecr.registry.BlockRegistry
import com.algorithmlx.ecr.registry.CreativeTabRegistry
import com.algorithmlx.ecr.registry.ItemRegistry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister

class NeoForgeCreativeTabRegistry(bus: IEventBus): CreativeTabRegistry {
    private val creativeTabs = DeferredRegister.create(BuiltInRegistries.CREATIVE_MODE_TAB, ModId)

    init {
        creativeTabs.register(bus)
    }

    override val items: CreativeModeTab by register("tab_items") {
        CreativeModeTab.builder()
            .icon { ItemStack(ItemRegistry.instance.elementalGem) }
            .build()
    }

    override val blocks: CreativeModeTab by register("tab_blocks") {
        CreativeModeTab.builder()
            .icon { ItemStack(BlockRegistry.instance.mithrilineFurnace) }
            .withTabsBefore(ResourceKey.create(Registries.CREATIVE_MODE_TAB, "tab_items".ecRL))
            .build()
    }

    private fun register(id: String, factory: () -> CreativeModeTab) = creativeTabs.register(id) { _ -> factory() }
}
