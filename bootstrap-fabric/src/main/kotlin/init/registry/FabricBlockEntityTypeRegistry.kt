package com.algorithmlx.ecr.fabric.init.registry

import com.algorithmlx.ecr.api.utils.ecRL
import com.algorithmlx.ecr.common.block.entity.ColdDistillerEntity
import com.algorithmlx.ecr.common.block.entity.EnvoyerBlockEntity
import com.algorithmlx.ecr.common.block.entity.MatrixDestructorEntity
import com.algorithmlx.ecr.common.block.entity.MithrilineFurnaceEntity
import com.algorithmlx.ecr.common.init.ECRModIDs
import com.algorithmlx.ecr.common.init.registry.BlockEntityTypeRegistry
import com.algorithmlx.ecr.common.init.registry.BlockRegistry
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType

object FabricBlockEntityTypeRegistry: BlockEntityTypeRegistry {
    override val mithrilineFurnace: BlockEntityType<MithrilineFurnaceEntity> = register(
        ECRModIDs.MITHRILINE_FURNACE,
        BlockEntityType(::MithrilineFurnaceEntity, setOf(BlockRegistry.instance.mithrilineFurnace))
    )
    override val envoyer: BlockEntityType<EnvoyerBlockEntity> = register(
        ECRModIDs.ENVOYER,
        BlockEntityType(::EnvoyerBlockEntity, setOf(BlockRegistry.instance.envoyer))
    )
    override val matrixDestructor: BlockEntityType<MatrixDestructorEntity> = register(
        ECRModIDs.MATRIX_DESTRUCTOR,
        BlockEntityType(::MatrixDestructorEntity, setOf(BlockRegistry.instance.matrixDestructor))
    )
    override val coldDistiller: BlockEntityType<ColdDistillerEntity> = register(
        ECRModIDs.COLD_DISTILLER,
        BlockEntityType(::ColdDistillerEntity, setOf(BlockRegistry.instance.coldDistiller))
    )

    private fun <B: BlockEntity> register(id: String, codec: BlockEntityType<B>) =
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id.ecRL, codec)
}
