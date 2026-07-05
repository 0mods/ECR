package com.algorithmlx.ecr.common.init.registry

import com.algorithmlx.ecr.common.components.SoulStoneComponent
import net.minecraft.core.component.DataComponentType

interface DataComponentRegistry {
    val soulStone: DataComponentType<SoulStoneComponent>

    companion object {
        lateinit var instance: DataComponentRegistry
    }
}
