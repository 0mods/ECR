package com.algorithmlx.ecr.fabric.init.registry

import com.algorithmlx.ecr.api.multiblock.assembled.AssembledMultiblockDefinition
import com.algorithmlx.ecr.api.utils.ecRL
import com.algorithmlx.ecr.api.multiblock.Multiblock
import com.algorithmlx.ecr.api.multiblock.MultiblockDefinitions
import com.algorithmlx.ecr.api.registries.ECRegistries
import com.algorithmlx.ecr.common.init.ECRModIDs
import com.algorithmlx.ecr.common.init.config.ECConfig
import com.algorithmlx.ecr.common.multiblocks.*
import com.algorithmlx.ecr.registry.MultiblockRegistry
import net.minecraft.core.Registry
import net.minecraft.resources.Identifier

object FabricMultiblockRegistry: MultiblockRegistry {
    private val codeMithrilineFurnace = register(ECRModIDs.MITHRILINE_FURNACE, MithrilineFurnaceMultiblock)
    private val codeSoulStone = register(ECRModIDs.SOUL_STONE, SoulStoneMultiblock)
    private val codeFlameCrystal = register(ECRModIDs.FLAME_CRYSTAL, FlameCrystal)
    private val codeWaterCrystal = register(ECRModIDs.WATER_CRYSTAL, WaterCrystal)
    private val codeEarthCrystal = register(ECRModIDs.EARTH_CRYSTAL, EarthCrystal)
    private val codeAirCrystal = register(ECRModIDs.AIR_CRYSTAL, AirCrystal)
    private val codeLightningCollector = register(ECRModIDs.LIGHTNING_COLLECTOR, LightningCollector)
    private val codeEnrichmentChamber = register(ECRModIDs.ENRICHMENT_CHAMBER, EnrichmentChamber)
    private val codeRayTower = register(ECRModIDs.RAY_TOWER, RayTowerMultiblock)
    private val codeMagicalTeleporter = register(ECRModIDs.MAGICAL_TELEPORTER, MagicalTeleporter)

    init {
        registerConfiguredMultiblocks(ECConfig.current.multiblocks.customMultiblockRegistryIds())
        registerConfiguredAssembledMultiblocks(
            ECConfig.current.multiblocks.customAssembledRegistryIds()
        )
    }

    override val mithrilineFurnace: Multiblock
        get() = MultiblockDefinitions[ECRModIDs.MITHRILINE_FURNACE.ecRL] ?: codeMithrilineFurnace
    override val soulStone: Multiblock
        get() = MultiblockDefinitions[ECRModIDs.SOUL_STONE.ecRL] ?: codeSoulStone
    override val flameCrystal: Multiblock
        get() = MultiblockDefinitions[ECRModIDs.FLAME_CRYSTAL.ecRL] ?: codeFlameCrystal
    override val waterCrystal: Multiblock
        get() = MultiblockDefinitions[ECRModIDs.WATER_CRYSTAL.ecRL] ?: codeWaterCrystal
    override val earthCrystal: Multiblock
        get() = MultiblockDefinitions[ECRModIDs.EARTH_CRYSTAL.ecRL] ?: codeEarthCrystal
    override val airCrystal: Multiblock
        get() = MultiblockDefinitions[ECRModIDs.AIR_CRYSTAL.ecRL] ?: codeAirCrystal
    override val lightningCollector: Multiblock
        get() = MultiblockDefinitions[ECRModIDs.LIGHTNING_COLLECTOR.ecRL] ?: codeLightningCollector
    override val enrichmentChamber: Multiblock
        get() = MultiblockDefinitions[ECRModIDs.ENRICHMENT_CHAMBER.ecRL] ?: codeEnrichmentChamber
    override val rayTower: AssembledMultiblockDefinition
        get() = MultiblockDefinitions.assembled(ECRModIDs.RAY_TOWER.ecRL) ?: codeRayTower
    override val magicalTeleporter: Multiblock
        get() = MultiblockDefinitions[ECRModIDs.MAGICAL_TELEPORTER.ecRL] ?: codeMagicalTeleporter

    private fun register(id: String, multiblock: Multiblock) = Registry.register(
        ECRegistries.MULTIBLOCK, id.ecRL, multiblock
    )

    private fun register(id: String, multiblock: AssembledMultiblockDefinition) = Registry.register(
        ECRegistries.ASSEMBLED_MULTIBLOCK, id.ecRL, multiblock
    )

    private fun registerConfiguredMultiblocks(ids: Set<Identifier>) {
        ids.forEach { id ->
            check(!ECRegistries.MULTIBLOCK.containsKey(id)) {
                "Configured custom multiblock $id is already registered; remove it from custom_ids " +
                    "and use its JSON file as an override"
            }
            Registry.register(ECRegistries.MULTIBLOCK, id, Multiblock.jsonOnly())
        }
    }

    private fun registerConfiguredAssembledMultiblocks(ids: Set<Identifier>) {
        ids.forEach { id ->
            check(!ECRegistries.ASSEMBLED_MULTIBLOCK.containsKey(id)) {
                "Configured custom assembled multiblock $id is already registered; remove it from " +
                    "custom_assembled_ids and use its JSON file as an override"
            }
            Registry.register(
                ECRegistries.ASSEMBLED_MULTIBLOCK,
                id,
                AssembledMultiblockDefinition.jsonOnly(id)
            )
        }
    }
}
