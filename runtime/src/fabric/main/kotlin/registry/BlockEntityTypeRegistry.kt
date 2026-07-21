package com.algorithmlx.ecr.registry

import com.algorithmlx.ecr.api.utils.ecRL
import com.algorithmlx.ecr.common.block.entity.ColdDistillerEntity
import com.algorithmlx.ecr.common.block.entity.MagicTableBlockEntity
import com.algorithmlx.ecr.common.block.entity.MatrixDestructorEntity
import com.algorithmlx.ecr.common.block.entity.MithrilineFurnaceEntity
import com.algorithmlx.ecr.common.init.ECRModIDs
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType

@Suppress("ACTUAL_WITHOUT_EXPECT", "unused")
actual object BlockEntityTypeRegistry {
    actual val mithrilineFurnace: BlockEntityType<MithrilineFurnaceEntity> = register(
        ECRModIDs.MITHRILINE_FURNACE,
        BlockEntityType(::MithrilineFurnaceEntity, setOf(BlockRegistry.mithrilineFurnace))
    )
    actual val magicTable: BlockEntityType<MagicTableBlockEntity> = register(
        ECRModIDs.MAGIC_TABLE,
        BlockEntityType(::MagicTableBlockEntity, setOf(BlockRegistry.magicTable))
    )
    actual val matrixDestructor: BlockEntityType<MatrixDestructorEntity> = register(
        ECRModIDs.MATRIX_DESTRUCTOR,
        BlockEntityType(::MatrixDestructorEntity, setOf(BlockRegistry.matrixDestructor))
    )
    actual val coldDistiller: BlockEntityType<ColdDistillerEntity> = register(
        ECRModIDs.COLD_DISTILLER,
        BlockEntityType(::ColdDistillerEntity, setOf(BlockRegistry.coldDistiller))
    )

    private fun <B: BlockEntity> register(id: String, type: BlockEntityType<B>) =
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id.ecRL, type)
}
