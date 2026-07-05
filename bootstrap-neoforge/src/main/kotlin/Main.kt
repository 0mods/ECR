package com.algorithmlx.ecr.neoforge

import com.algorithmlx.ecr.api.ModId
import com.algorithmlx.ecr.neoforge.init.forgeStarter
import com.algorithmlx.ecr.neoforge.init.initEvents
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.common.Mod

@Mod(ModId)
class Main(eventBus: IEventBus) {
    init {
        initEvents(eventBus)
        forgeStarter(eventBus)
    }
}
