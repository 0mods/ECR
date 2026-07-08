package com.algorithmlx.ecr.neoforge

import com.algorithmlx.ecr.api.ModId
import com.algorithmlx.ecr.neoforge.init.forgeStarter
import com.algorithmlx.ecr.neoforge.init.initEvents
import com.algorithmlx.ecr.neoforge.init.initForgeEvents
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.common.NeoForge

@Mod(ModId)
class Main(eventBus: IEventBus) {
    init {
        initForgeEvents(NeoForge.EVENT_BUS)
        initEvents(eventBus)
        forgeStarter(eventBus)
    }
}
