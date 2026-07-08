package com.algorithmlx.ecr.common.init.registry

import net.minecraft.world.item.CreativeModeTab

interface CreativeTabs {
    val items: CreativeModeTab
    val blocks: CreativeModeTab

    companion object {
        lateinit var instance: CreativeTabs
    }
}
