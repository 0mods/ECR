package com.algorithmlx.ecr.neoforge.init.registry

import com.algorithmlx.ecr.api.ModId
import com.algorithmlx.ecr.common.block.entity.*
import com.algorithmlx.ecr.common.block.entity.enrichment.EnrichmentChamberControllerEntity
import com.algorithmlx.ecr.common.block.entity.enrichment.EnrichmentChamberExtractorEntity
import com.algorithmlx.ecr.common.block.entity.enrichment.EnrichmentChamberReceiverEntity
import com.algorithmlx.ecr.common.init.ECRModIDs
import com.algorithmlx.ecr.registry.BlockEntityTypeRegistry
import com.algorithmlx.ecr.registry.BlockRegistry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister

class NeoForgeBlockEntityTypeRegistry(
    bus: IEventBus,
) : BlockEntityTypeRegistry {
    private val blockEntityType = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, ModId)

    init {
        blockEntityType.register(bus)
    }

    override val assembledMultiblockPart: BlockEntityType<AssembledMultiblockPartBlockEntity> by register(ECRModIDs.ASSEMBLED_MULTIBLOCK_PART) { BlockEntityType(::AssembledMultiblockPartBlockEntity, setOf(BlockRegistry.instance.assembledMultiblockPart)) }
    override val mithrilineFurnace: BlockEntityType<MithrilineFurnaceEntity> by register(ECRModIDs.MITHRILINE_FURNACE) { BlockEntityType(::MithrilineFurnaceEntity, setOf(BlockRegistry.instance.mithrilineFurnace)) }
    override val magicTable: BlockEntityType<MagicTableBlockEntity> by register(ECRModIDs.MAGIC_TABLE) { BlockEntityType(::MagicTableBlockEntity, setOf(BlockRegistry.instance.magicTable)) }
    override val magicalTeleporter: BlockEntityType<MagicalTeleporterEntity> by register(ECRModIDs.MAGICAL_TELEPORTER) { BlockEntityType(::MagicalTeleporterEntity, setOf(BlockRegistry.instance.magicalTeleporter)) }
    override val matrixDestructor: BlockEntityType<MatrixDestructorEntity> by register(ECRModIDs.MATRIX_DESTRUCTOR) { BlockEntityType(::MatrixDestructorEntity, setOf(BlockRegistry.instance.matrixDestructor)) }
    override val coldDistiller: BlockEntityType<ColdDistillerEntity> by register(ECRModIDs.COLD_DISTILLER) { BlockEntityType(::ColdDistillerEntity, setOf(BlockRegistry.instance.coldDistiller)) }
    override val heatGenerator: BlockEntityType<HeatGeneratorEntity> by register(ECRModIDs.HEAT_GENERATOR) { BlockEntityType(::HeatGeneratorEntity, setOf(BlockRegistry.instance.heatGenerator)) }
    override val enrichmentChamberController: BlockEntityType<EnrichmentChamberControllerEntity> by register(ECRModIDs.ENRICHMENT_CHAMBER_CONTROLLER) { BlockEntityType(::EnrichmentChamberControllerEntity, setOf(BlockRegistry.instance.enrichmentChamberController)) }
    override val enrichmentChamberExtractor: BlockEntityType<EnrichmentChamberExtractorEntity> by register(ECRModIDs.ENRICHMENT_CHAMBER_EXTRACTOR) { BlockEntityType(::EnrichmentChamberExtractorEntity, setOf(BlockRegistry.instance.enrichmentChamberExtractor)) }
    override val enrichmentChamberReceiver: BlockEntityType<EnrichmentChamberReceiverEntity> by register(ECRModIDs.ENRICHMENT_CHAMBER_RECEIVER) { BlockEntityType(::EnrichmentChamberReceiverEntity, setOf(BlockRegistry.instance.enrichmentChamberReceiver)) }
    override val rayTower: BlockEntityType<RayTowerEntity> by register(ECRModIDs.RAY_TOWER) { BlockEntityType(::RayTowerEntity, setOf(BlockRegistry.instance.rayTower)) }
    override val creativeMRUSource: BlockEntityType<CreativeMRUSourceEntity> by register(ECRModIDs.CREATIVE_MRU_SOURCE) { BlockEntityType(::CreativeMRUSourceEntity, setOf(BlockRegistry.instance.creativeMRUSource)) }

    private fun <T: BlockEntity> register(id: String, factory: () -> BlockEntityType<T>) = blockEntityType.register(id) { _ -> factory() }
}
