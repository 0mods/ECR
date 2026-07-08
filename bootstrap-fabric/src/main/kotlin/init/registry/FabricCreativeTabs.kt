package com.algorithmlx.ecr.fabric.init.registry

import com.algorithmlx.ecr.api.ecRL
import com.algorithmlx.ecr.common.init.registry.BlockRegistry
import com.algorithmlx.ecr.common.init.registry.CreativeTabs
import com.algorithmlx.ecr.common.init.registry.ItemRegistry
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack

object FabricCreativeTabs: CreativeTabs {
    override val items: CreativeModeTab = register(
        "tab_items",
        FabricCreativeModeTab.builder()
            .icon { ItemStack(ItemRegistry.instance.elementalGem) }
            .build()
    )
    override val blocks: CreativeModeTab = register(
        "tab_blocks",
        FabricCreativeModeTab.builder().icon { ItemStack(BlockRegistry.instance.mithrilineFurnace) }
            .build()
    )

    private fun register(id: String, tab: CreativeModeTab) = Registry.register(
        BuiltInRegistries.CREATIVE_MODE_TAB, id.ecRL, tab
    )
}
