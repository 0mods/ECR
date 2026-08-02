package com.algorithmlx.ecr.fabric.init.registry

import com.algorithmlx.ecr.api.utils.ecRL
import com.algorithmlx.ecr.common.block.ClusterBlock
import com.algorithmlx.ecr.common.block.AssembledMultiblockPartBlock
import com.algorithmlx.ecr.common.block.ColdDistiller
import com.algorithmlx.ecr.common.block.CrystalBlock
import com.algorithmlx.ecr.common.block.EnrichmentChamberController
import com.algorithmlx.ecr.common.block.EnrichmentChamberExtractor
import com.algorithmlx.ecr.common.block.EnrichmentChamberReceiver
import com.algorithmlx.ecr.common.block.MagicTable
import com.algorithmlx.ecr.common.block.MatrixDestructor
import com.algorithmlx.ecr.common.block.MithrilineFurnace
import com.algorithmlx.ecr.common.block.RayTower
import com.algorithmlx.ecr.common.block.SolarPrism
import com.algorithmlx.ecr.common.init.ECRModIDs
import com.algorithmlx.ecr.registry.BlockCodecRegistry
import com.mojang.serialization.MapCodec
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour

object FabricBlockCodecRegistry: BlockCodecRegistry {
    override val assembledMultiblockPart: MapCodec<AssembledMultiblockPartBlock> = register(
        ECRModIDs.ASSEMBLED_MULTIBLOCK_PART,
        BlockBehaviour.simpleCodec(::AssembledMultiblockPartBlock)
    )
    override val solarPrism: MapCodec<SolarPrism> = register(
        ECRModIDs.SOLAR_PRISM,
        BlockBehaviour.simpleCodec(::SolarPrism)
    )
    override val clusterBlock: MapCodec<ClusterBlock> = register(
        ECRModIDs.CLUSTER,
        BlockBehaviour.simpleCodec(::ClusterBlock)
    )
    override val crystalBlock: MapCodec<CrystalBlock> = register(
        ECRModIDs.CRYSTAL,
        BlockBehaviour.simpleCodec(::CrystalBlock)
    )
    override val mithrilineFurnace: MapCodec<MithrilineFurnace> = register(
        ECRModIDs.MITHRILINE_FURNACE,
        BlockBehaviour.simpleCodec(::MithrilineFurnace)
    )
    override val magicTable: MapCodec<MagicTable> = register(
        ECRModIDs.MAGIC_TABLE,
        BlockBehaviour.simpleCodec(::MagicTable)
    )
    override val matrixDestructor: MapCodec<MatrixDestructor> = register(
        ECRModIDs.MATRIX_DESTRUCTOR,
        BlockBehaviour.simpleCodec(::MatrixDestructor)
    )
    override val coldDistiller: MapCodec<ColdDistiller> = register(
        ECRModIDs.COLD_DISTILLER,
        BlockBehaviour.simpleCodec(::ColdDistiller)
    )
    override val enrichmentChamberController: MapCodec<EnrichmentChamberController> = register(
        ECRModIDs.ENRICHMENT_CHAMBER_CONTROLLER,
        BlockBehaviour.simpleCodec(::EnrichmentChamberController)
    )
    override val enrichmentChamberExtractor: MapCodec<EnrichmentChamberExtractor> = register(
        ECRModIDs.ENRICHMENT_CHAMBER_EXTRACTOR,
        BlockBehaviour.simpleCodec(::EnrichmentChamberExtractor)
    )
    override val enrichmentChamberReceiver: MapCodec<EnrichmentChamberReceiver> = register(
        ECRModIDs.ENRICHMENT_CHAMBER_RECEIVER,
        BlockBehaviour.simpleCodec(::EnrichmentChamberReceiver)
    )
    override val rayTower: MapCodec<RayTower> = register(
        ECRModIDs.RAY_TOWER,
        BlockBehaviour.simpleCodec(::RayTower)
    )

    private fun <B: Block> register(id: String, codec: MapCodec<B>) =
        Registry.register(BuiltInRegistries.BLOCK_TYPE, id.ecRL, codec)
}
