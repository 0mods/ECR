package com.algorithmlx.ecr.fabric.init

import com.algorithmlx.ecr.api.ModId
import com.algorithmlx.ecr.api.item.HasSubItem
import com.algorithmlx.ecr.api.item.NoTab
import com.algorithmlx.ecr.common.init.events.ECEvents
import com.algorithmlx.ecr.common.init.registry.CreativeTabRegistry
import com.algorithmlx.ecr.common.item.NamedBlockItem
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents
import net.fabricmc.fabric.api.event.player.PlayerPickItemEvents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.ItemStack

object FabricEventInit {
    fun initEvents() {
        tooltipEvent()
        tabEvent()
    }

    private fun tooltipEvent() {
        ItemTooltipCallback.EVENT.register { stack, _, _, components ->
            ECEvents.itemTooltip(stack, components)
        }
    }

    private fun tabEvent() {
        CreativeModeTabEvents.MODIFY_OUTPUT_ALL.register { tab, output ->
            BuiltInRegistries.ITEM.keySet().filter { it.namespace == ModId }.forEach {
                val item = BuiltInRegistries.ITEM.getOptional(it).get()
                if (tab == CreativeTabRegistry.instance.blocks) {
                    if ((item is BlockItem || item is NamedBlockItem) && item.block !is NoTab)
                        output.accept(item)
                    return@forEach
                }

                if (BuiltInRegistries.BLOCK.getOptional(it).isPresent) return@forEach

                if (item is NoTab || tab != CreativeTabRegistry.instance.items) return@forEach

                if (item is HasSubItem) {
                    item.addSubItems(ItemStack(item)).forEach { stack ->
                        output.accept(stack)
                    }

                    return@forEach
                }

                output.accept(item)
            }
        }
    }
}
