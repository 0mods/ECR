package com.algorithmlx.ecr.common.init

import com.algorithmlx.ecr.common.block.entity.MithrilineFurnaceEntity
import net.minecraft.world.level.block.entity.BlockEntityType

interface BlockEntityTypeRegistry {
    val mithrilineFurnaceEntity: BlockEntityType<MithrilineFurnaceEntity>

    companion object {
        @JvmStatic
        val instance: BlockEntityTypeRegistry = UnionRegistry.instance
    }
}
