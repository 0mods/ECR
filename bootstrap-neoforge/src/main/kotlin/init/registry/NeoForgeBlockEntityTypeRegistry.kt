package com.algorithmlx.ecr.neoforge.init.registry

import com.algorithmlx.ecr.api.ModId
import com.algorithmlx.ecr.common.block.entity.MithrilineFurnaceEntity
import com.algorithmlx.ecr.common.init.ECRModIDs
import com.algorithmlx.ecr.common.init.registry.BlockEntityTypeRegistry
import com.algorithmlx.ecr.common.init.registry.BlockRegistry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.level.block.entity.BlockEntityType
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister

class NeoForgeBlockEntityTypeRegistry(bus: IEventBus): BlockEntityTypeRegistry {
    private val blockEntityType = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, ModId)

    init {
        blockEntityType.register(bus)
    }

    private val mithrilineFurnaceEntity = blockEntityType.register(ECRModIDs.MITHRILINE_FURNACE) { _ ->
        BlockEntityType(::MithrilineFurnaceEntity, setOf(BlockRegistry.instance.mithrilineFurnace))
    }

    override val mithrilineFurnace: BlockEntityType<MithrilineFurnaceEntity> by lazy { mithrilineFurnaceEntity.get() }
}