package com.algorithmlx.ecr.neoforge.init.registry

import com.algorithmlx.ecr.api.ModId
import com.algorithmlx.ecr.common.block.*
import com.algorithmlx.ecr.common.block.ClusterBlock
import com.algorithmlx.ecr.common.init.ECRModIDs
import com.algorithmlx.ecr.registry.BlockCodecRegistry
import com.mojang.serialization.MapCodec
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister

class NeoForgeBlockCodecRegistry(
    bus: IEventBus,
) : BlockCodecRegistry {
    private val blockTypes = DeferredRegister.create(BuiltInRegistries.BLOCK_TYPE, ModId)

    init {
        blockTypes.register(bus)
    }

    override val assembledMultiblockPart: MapCodec<AssembledMultiblockPartBlock> by register(ECRModIDs.ASSEMBLED_MULTIBLOCK_PART) { BlockBehaviour.simpleCodec(::AssembledMultiblockPartBlock) }
    override val solarPrism: MapCodec<SolarPrism> by register(ECRModIDs.SOLAR_PRISM) { BlockBehaviour.simpleCodec(::SolarPrism) }
    override val clusterBlock: MapCodec<ClusterBlock> by register(ECRModIDs.CLUSTER) { BlockBehaviour.simpleCodec(::ClusterBlock) }
    override val crystalBlock: MapCodec<CrystalBlock> by register(ECRModIDs.CRYSTAL) { BlockBehaviour.simpleCodec(::CrystalBlock) }
    override val mithrilineFurnace: MapCodec<MithrilineFurnace> by register(ECRModIDs.MITHRILINE_FURNACE) { BlockBehaviour.simpleCodec(::MithrilineFurnace) }
    override val magicTable: MapCodec<MagicTable> by register(ECRModIDs.MAGIC_TABLE) { BlockBehaviour.simpleCodec(::MagicTable) }
    override val matrixDestructor: MapCodec<MatrixDestructor> by register(ECRModIDs.MATRIX_DESTRUCTOR) { BlockBehaviour.simpleCodec(::MatrixDestructor) }
    override val coldDistiller: MapCodec<ColdDistiller> by register(ECRModIDs.COLD_DISTILLER) { BlockBehaviour.simpleCodec(::ColdDistiller) }
    override val heatGenerator: MapCodec<HeatGenerator> by register(ECRModIDs.HEAT_GENERATOR) { BlockBehaviour.simpleCodec(::HeatGenerator) }
    override val enrichmentChamberController: MapCodec<EnrichmentChamberController> by register(ECRModIDs.ENRICHMENT_CHAMBER_CONTROLLER) { BlockBehaviour.simpleCodec(::EnrichmentChamberController) }
    override val enrichmentChamberExtractor: MapCodec<EnrichmentChamberExtractor> by register(ECRModIDs.ENRICHMENT_CHAMBER_EXTRACTOR) { BlockBehaviour.simpleCodec(::EnrichmentChamberExtractor) }
    override val enrichmentChamberReceiver: MapCodec<EnrichmentChamberReceiver> by register(ECRModIDs.ENRICHMENT_CHAMBER_RECEIVER) { BlockBehaviour.simpleCodec(::EnrichmentChamberReceiver) }
    override val rayTower: MapCodec<RayTower> by register(ECRModIDs.RAY_TOWER) { BlockBehaviour.simpleCodec(::RayTower) }
    override val creativeMRUSource: MapCodec<CreativeMRUSource> by register(ECRModIDs.CREATIVE_MRU_SOURCE) { BlockBehaviour.simpleCodec(::CreativeMRUSource) }

    private fun <B: Block> register(id: String, codec: () -> MapCodec<B>) = blockTypes.register(id) { _ -> codec() }
}
