package com.algorithmlx.ecr.fabric.init.registry

import com.algorithmlx.ecr.api.ModId
import com.algorithmlx.ecr.api.utils.ecRL
import com.algorithmlx.ecr.common.init.ECRModIDs
import com.algorithmlx.ecr.registry.BlockRegistry
import com.algorithmlx.ecr.registry.CreativeTabRegistry
import com.algorithmlx.ecr.registry.ItemRegistry
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack

object FabricCreativeTabRegistry : CreativeTabRegistry {
    override val items: CreativeModeTab = register(
        ECRModIDs.TAB_ITEMS,
        FabricCreativeModeTab.builder()
            .icon { ItemStack(ItemRegistry.instance.elementalGem) }
            .title(Component.translatable("itemGroup.$ModId.items"))
            .build()
    )
    override val blocks: CreativeModeTab = register(
        ECRModIDs.TAB_BLOCKS,
        FabricCreativeModeTab.builder()
            .icon { ItemStack(BlockRegistry.instance.mithrilineFurnace) }
            .title(Component.translatable("itemGroup.$ModId.blocks"))
            .build()
    )

    private fun register(id: String, tab: CreativeModeTab) = Registry.register(
        BuiltInRegistries.CREATIVE_MODE_TAB, id.ecRL, tab
    )
}
