package com.algorithmlx.ecr.common.init

import com.algorithmlx.ecr.common.components.SoulStoneComponent
import net.minecraft.core.component.DataComponentType

interface DataComponentRegistry {
    val soulStoneComponent: DataComponentType<SoulStoneComponent>

    companion object {
        @JvmStatic
        val instance: DataComponentRegistry = UnionRegistry.instance
    }
}
