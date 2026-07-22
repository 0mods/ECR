package com.algorithmlx.ecr.registry

import net.minecraft.world.item.CreativeModeTab

interface CreativeTabRegistry {
    val items: CreativeModeTab
    val blocks: CreativeModeTab

    companion object {
        @JvmStatic
        lateinit var instance: CreativeTabRegistry
    }
}
