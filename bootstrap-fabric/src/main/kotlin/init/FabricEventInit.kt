package com.algorithmlx.ecr.fabric.init

import com.algorithmlx.ecr.api.ModId
import com.algorithmlx.ecr.api.item.HasSubItem
import com.algorithmlx.ecr.api.item.NoTab
import com.algorithmlx.ecr.common.init.events.ECEvents
import com.algorithmlx.ecr.common.init.registry.CreativeTabRegistry
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.ItemStack

object FabricEventInit {
    fun initEvents() {
        tooltipEvent()
    }

    private fun tooltipEvent() {
        ItemTooltipCallback.EVENT.register { stack, _, _, components ->
            ECEvents.itemTooltip(stack, components)
        }

        CreativeModeTabEvents.MODIFY_OUTPUT_ALL.register { tab, output ->
            BuiltInRegistries.ITEM.keySet().filter { it.namespace == ModId }.forEach {
                val item = BuiltInRegistries.ITEM.getOptional(it).get()
                if (item is BlockItem && tab == CreativeTabRegistry.instance.blocks) {
                    if (item.block is NoTab) return@forEach
                    output.accept(item)
                    return@forEach
                }

                if (tab != CreativeTabRegistry.instance.items || item is NoTab) return@forEach

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
