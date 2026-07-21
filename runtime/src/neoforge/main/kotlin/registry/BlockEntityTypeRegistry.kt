package com.algorithmlx.ecr.registry

import com.algorithmlx.ecr.api.ModId
import com.algorithmlx.ecr.common.block.entity.*
import com.algorithmlx.ecr.common.init.ECRModIDs
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.level.block.entity.BlockEntityType
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister

@Suppress("ACTUAL_WITHOUT_EXPECT", "unused")
actual object BlockEntityTypeRegistry {
    private val blockEntityType = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, ModId)

    fun init(bus: IEventBus) {
        blockEntityType.register(bus)
    }

    private val mithrilineFurnaceEntity = blockEntityType.register(ECRModIDs.MITHRILINE_FURNACE) { _ ->
        BlockEntityType(::MithrilineFurnaceEntity, setOf(BlockRegistry.mithrilineFurnace))
    }
    private val magicTableEntity = blockEntityType.register(ECRModIDs.MAGIC_TABLE) { _ ->
        BlockEntityType(::MagicTableBlockEntity, setOf(BlockRegistry.magicTable))
    }
    private val matrixDestructorEntity = blockEntityType.register(ECRModIDs.MATRIX_DESTRUCTOR) { _ ->
        BlockEntityType(::MatrixDestructorEntity, setOf(BlockRegistry.matrixDestructor))
    }
    private val coldDistillerEntity = blockEntityType.register(ECRModIDs.COLD_DISTILLER) { _ ->
        BlockEntityType(::ColdDistillerEntity, setOf(BlockRegistry.coldDistiller))
    }

    actual val mithrilineFurnace: BlockEntityType<MithrilineFurnaceEntity> by lazy { mithrilineFurnaceEntity.get() }
    actual val magicTable: BlockEntityType<MagicTableBlockEntity> by lazy { magicTableEntity.get() }
    actual val matrixDestructor: BlockEntityType<MatrixDestructorEntity> by lazy { matrixDestructorEntity.get() }
    actual val coldDistiller: BlockEntityType<ColdDistillerEntity> by lazy { coldDistillerEntity.get() }
}
