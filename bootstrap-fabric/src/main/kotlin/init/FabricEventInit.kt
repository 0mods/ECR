package com.algorithmlx.ecr.fabric.init

import com.algorithmlx.ecr.common.init.events.ECEvents
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback

object FabricEventInit {
    fun initEvents() {
        tooltipEvent()
    }

    private fun tooltipEvent() {
        ItemTooltipCallback.EVENT.register { stack, _, _, components ->
            ECEvents.itemTooltip(stack, components)
        }
    }
}