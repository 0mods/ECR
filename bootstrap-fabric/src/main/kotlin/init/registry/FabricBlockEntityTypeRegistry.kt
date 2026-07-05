package com.algorithmlx.ecr.fabric.init.registry

import com.algorithmlx.ecr.api.ecRL
import com.algorithmlx.ecr.common.block.entity.MithrilineFurnaceEntity
import com.algorithmlx.ecr.common.init.registry.BlockEntityTypeRegistry
import com.algorithmlx.ecr.common.init.registry.BlockRegistry
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType

object FabricBlockEntityTypeRegistry: BlockEntityTypeRegistry {
    override val mithrilineFurnace: BlockEntityType<MithrilineFurnaceEntity> = register(
        "mithriline_furnace",
        BlockEntityType(::MithrilineFurnaceEntity, setOf(BlockRegistry.instance.mithrilineFurnace))
    )

    private fun <B: BlockEntity> register(id: String, codec: BlockEntityType<B>) =
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id.ecRL, codec)
}
