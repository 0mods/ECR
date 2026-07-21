package com.algorithmlx.ecr.registry

import com.algorithmlx.ecr.common.block.entity.ColdDistillerEntity
import com.algorithmlx.ecr.common.block.entity.MagicTableBlockEntity
import com.algorithmlx.ecr.common.block.entity.MatrixDestructorEntity
import com.algorithmlx.ecr.common.block.entity.MithrilineFurnaceEntity
import net.minecraft.world.level.block.entity.BlockEntityType

expect object BlockEntityTypeRegistry {
    val mithrilineFurnace: BlockEntityType<MithrilineFurnaceEntity>
    val magicTable: BlockEntityType<MagicTableBlockEntity>
    val matrixDestructor: BlockEntityType<MatrixDestructorEntity>
    val coldDistiller: BlockEntityType<ColdDistillerEntity>
}
