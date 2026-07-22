package com.algorithmlx.ecr.neoforge.init.registry

import com.algorithmlx.ecr.api.ModId
import com.algorithmlx.ecr.common.block.entity.*
import com.algorithmlx.ecr.common.init.ECRModIDs
import com.algorithmlx.ecr.registry.BlockEntityTypeRegistry
import com.algorithmlx.ecr.registry.BlockRegistry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.level.block.entity.BlockEntityType
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister

object NeoForgeBlockEntityTypeRegistry : BlockEntityTypeRegistry {
    private val blockEntityType = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, ModId)

    fun init(bus: IEventBus) {
        blockEntityType.register(bus)
    }

    private val mithrilineFurnaceEntity = blockEntityType.register(ECRModIDs.MITHRILINE_FURNACE) { _ ->
        BlockEntityType(::MithrilineFurnaceEntity, setOf(BlockRegistry.instance.mithrilineFurnace))
    }
    private val magicTableEntity = blockEntityType.register(ECRModIDs.MAGIC_TABLE) { _ ->
        BlockEntityType(::MagicTableBlockEntity, setOf(BlockRegistry.instance.magicTable))
    }
    private val magicalTeleporterEntity = blockEntityType.register(ECRModIDs.MAGICAL_TELEPORTER) { _ ->
        BlockEntityType(::MagicalTeleporterEntity, setOf(BlockRegistry.instance.magicalTeleporter))
    }
    private val matrixDestructorEntity = blockEntityType.register(ECRModIDs.MATRIX_DESTRUCTOR) { _ ->
        BlockEntityType(::MatrixDestructorEntity, setOf(BlockRegistry.instance.matrixDestructor))
    }
    private val coldDistillerEntity = blockEntityType.register(ECRModIDs.COLD_DISTILLER) { _ ->
        BlockEntityType(::ColdDistillerEntity, setOf(BlockRegistry.instance.coldDistiller))
    }

    override val mithrilineFurnace: BlockEntityType<MithrilineFurnaceEntity> by lazy { mithrilineFurnaceEntity.get() }
    override val magicTable: BlockEntityType<MagicTableBlockEntity> by lazy { magicTableEntity.get() }
    override val magicalTeleporter: BlockEntityType<MagicalTeleporterEntity> by lazy { magicalTeleporterEntity.get() }
    override val matrixDestructor: BlockEntityType<MatrixDestructorEntity> by lazy { matrixDestructorEntity.get() }
    override val coldDistiller: BlockEntityType<ColdDistillerEntity> by lazy { coldDistillerEntity.get() }
}
