package com.algorithmlx.ecr.registry

import net.minecraft.world.item.CreativeModeTab

expect object CreativeTabRegistry {
    val items: CreativeModeTab
    val blocks: CreativeModeTab
}
