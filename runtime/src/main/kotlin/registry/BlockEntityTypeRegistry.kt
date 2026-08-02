package com.algorithmlx.ecr.registry

import com.algorithmlx.ecr.common.block.entity.ColdDistillerEntity
import com.algorithmlx.ecr.common.block.entity.AssembledMultiblockPartBlockEntity
import com.algorithmlx.ecr.common.block.entity.EnrichmentChamberControllerEntity
import com.algorithmlx.ecr.common.block.entity.EnrichmentChamberExtractorEntity
import com.algorithmlx.ecr.common.block.entity.EnrichmentChamberReceiverEntity
import com.algorithmlx.ecr.common.block.entity.MagicTableBlockEntity
import com.algorithmlx.ecr.common.block.entity.MagicalTeleporterEntity
import com.algorithmlx.ecr.common.block.entity.MatrixDestructorEntity
import com.algorithmlx.ecr.common.block.entity.MithrilineFurnaceEntity
import com.algorithmlx.ecr.common.block.entity.RayTowerEntity
import net.minecraft.world.level.block.entity.BlockEntityType

interface BlockEntityTypeRegistry {
    val assembledMultiblockPart: BlockEntityType<AssembledMultiblockPartBlockEntity>
    val mithrilineFurnace: BlockEntityType<MithrilineFurnaceEntity>
    val magicTable: BlockEntityType<MagicTableBlockEntity>
    val matrixDestructor: BlockEntityType<MatrixDestructorEntity>
    val coldDistiller: BlockEntityType<ColdDistillerEntity>
    val magicalTeleporter: BlockEntityType<MagicalTeleporterEntity>
    val enrichmentChamberController: BlockEntityType<EnrichmentChamberControllerEntity>
    val enrichmentChamberExtractor: BlockEntityType<EnrichmentChamberExtractorEntity>
    val enrichmentChamberReceiver: BlockEntityType<EnrichmentChamberReceiverEntity>
    val rayTower: BlockEntityType<RayTowerEntity>

    companion object {
        @JvmStatic
        lateinit var instance: BlockEntityTypeRegistry
    }
}
