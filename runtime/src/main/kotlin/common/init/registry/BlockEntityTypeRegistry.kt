package com.algorithmlx.ecr.common.init.registry

import com.algorithmlx.ecr.common.block.entity.ColdDistillerEntity
import com.algorithmlx.ecr.common.block.entity.MagicTableBlockEntity
import com.algorithmlx.ecr.common.block.entity.MatrixDestructorEntity
import com.algorithmlx.ecr.common.block.entity.MithrilineFurnaceEntity
import net.minecraft.world.level.block.entity.BlockEntityType

interface BlockEntityTypeRegistry {
    val mithrilineFurnace: BlockEntityType<MithrilineFurnaceEntity>
    val envoyer: BlockEntityType<MagicTableBlockEntity>
    val matrixDestructor: BlockEntityType<MatrixDestructorEntity>
    val coldDistiller: BlockEntityType<ColdDistillerEntity>

    companion object {
        lateinit var instance: BlockEntityTypeRegistry
    }
}
