package com.algorithmlx.ecr.fabric.init.registry

import com.algorithmlx.ecr.api.utils.ecRL
import com.algorithmlx.ecr.common.block.entity.ColdDistillerEntity
import com.algorithmlx.ecr.common.block.entity.MagicTableBlockEntity
import com.algorithmlx.ecr.common.block.entity.MagicalTeleporterEntity
import com.algorithmlx.ecr.common.block.entity.MatrixDestructorEntity
import com.algorithmlx.ecr.common.block.entity.MithrilineFurnaceEntity
import com.algorithmlx.ecr.common.init.ECRModIDs
import com.algorithmlx.ecr.registry.BlockEntityTypeRegistry
import com.algorithmlx.ecr.registry.BlockRegistry
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType

object FabricBlockEntityTypeRegistry : BlockEntityTypeRegistry {
    override val mithrilineFurnace: BlockEntityType<MithrilineFurnaceEntity> = register(
        ECRModIDs.MITHRILINE_FURNACE,
        BlockEntityType(::MithrilineFurnaceEntity, setOf(BlockRegistry.instance.mithrilineFurnace))
    )
    override val magicTable: BlockEntityType<MagicTableBlockEntity> = register(
        ECRModIDs.MAGIC_TABLE,
        BlockEntityType(::MagicTableBlockEntity, setOf(BlockRegistry.instance.magicTable))
    )
    override val magicalTeleporter: BlockEntityType<MagicalTeleporterEntity> = register(
        ECRModIDs.MAGICAL_TELEPORTER,
        BlockEntityType(::MagicalTeleporterEntity, setOf(BlockRegistry.instance.magicalTeleporter))
    )
    override val matrixDestructor: BlockEntityType<MatrixDestructorEntity> = register(
        ECRModIDs.MATRIX_DESTRUCTOR,
        BlockEntityType(::MatrixDestructorEntity, setOf(BlockRegistry.instance.matrixDestructor))
    )
    override val coldDistiller: BlockEntityType<ColdDistillerEntity> = register(
        ECRModIDs.COLD_DISTILLER,
        BlockEntityType(::ColdDistillerEntity, setOf(BlockRegistry.instance.coldDistiller))
    )

    private fun <B: BlockEntity> register(id: String, type: BlockEntityType<B>) =
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id.ecRL, type)
}
