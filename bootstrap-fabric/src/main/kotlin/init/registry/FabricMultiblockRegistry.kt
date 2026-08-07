package com.algorithmlx.ecr.fabric.init.registry

import com.algorithmlx.ecr.api.assembled.AssembledMultiblockDefinition
import com.algorithmlx.ecr.api.utils.ecRL
import com.algorithmlx.ecr.api.multiblock.Multiblock
import com.algorithmlx.ecr.api.registries.ECRegistries
import com.algorithmlx.ecr.common.init.ECRModIDs
import com.algorithmlx.ecr.common.multiblocks.*
import com.algorithmlx.ecr.registry.MultiblockRegistry
import net.minecraft.core.Registry

object FabricMultiblockRegistry: MultiblockRegistry {
    override val mithrilineFurnace: Multiblock = register(ECRModIDs.MITHRILINE_FURNACE, MithrilineFurnaceMultiblock)
    override val soulStone: Multiblock = register(ECRModIDs.SOUL_STONE, SoulStoneMultiblock)
    override val flameCrystal: Multiblock = register(ECRModIDs.FLAME_CRYSTAL, FlameCrystal)
    override val waterCrystal: Multiblock = register(ECRModIDs.WATER_CRYSTAL, WaterCrystal)
    override val earthCrystal: Multiblock = register(ECRModIDs.EARTH_CRYSTAL, EarthCrystal)
    override val airCrystal: Multiblock = register(ECRModIDs.AIR_CRYSTAL, AirCrystal)
    override val lightningCollector: Multiblock = register(ECRModIDs.LIGHTNING_COLLECTOR, LightningCollector)
    override val enrichmentChamber: Multiblock = register(ECRModIDs.ENRICHMENT_CHAMBER, EnrichmentChamber)
    override val rayTower: AssembledMultiblockDefinition = register(ECRModIDs.RAY_TOWER, RayTowerMultiblock)
    override val magicalTeleporter: Multiblock = register(ECRModIDs.MAGICAL_TELEPORTER, MagicalTeleporter)

    private fun register(id: String, multiblock: Multiblock) = Registry.register(
        ECRegistries.MULTIBLOCK, id.ecRL, multiblock
    )

    private fun register(id: String, multiblock: AssembledMultiblockDefinition) = Registry.register(
        ECRegistries.ASSEMBLED_MULTIBLOCK, id.ecRL, multiblock
    )
}
