package com.algorithmlx.ecr.common.init.registry

import com.algorithmlx.ecr.common.block.entity.EnvoyerBlockEntity
import com.algorithmlx.ecr.common.block.entity.MithrilineFurnaceEntity
import net.minecraft.world.level.block.entity.BlockEntityType

interface BlockEntityTypeRegistry {
    val mithrilineFurnace: BlockEntityType<MithrilineFurnaceEntity>
    val envoyer: BlockEntityType<EnvoyerBlockEntity>

    companion object {
        lateinit var instance: BlockEntityTypeRegistry
    }
}
