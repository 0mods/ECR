package com.algorithmlx.ecr.neoforge.init

import com.algorithmlx.ecr.client.screen.MithrilineFurnaceScreen
import com.algorithmlx.ecr.common.init.registry.MenuTypeRegistry
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent

object NeoForgeClientInit {
    fun init(bus: IEventBus) {
        bus.register(::onMenuScreen)
    }

    private fun onMenuScreen(event: RegisterMenuScreensEvent) {
        event.register(MenuTypeRegistry.instance.mithrilineFurnace, ::MithrilineFurnaceScreen)
    }
}
