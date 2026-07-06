package com.algorithmlx.ecr.client.book

import com.algorithmlx.ecr.client.screen.ResearchBookScreen
import com.algorithmlx.ecr.common.item.ResearchBookHooks
import net.minecraft.client.Minecraft

object ResearchBookClient {
    @JvmStatic
    fun init() {
        BookDefaultRenderers.init()
        ResearchBookHooks.open = { Minecraft.getInstance().setScreenAndShow(ResearchBookScreen()) }
    }
}
