package com.algorithmlx.ecr.neoforge.init.registry

import com.algorithmlx.ecr.api.ModId
import com.algorithmlx.ecr.api.assembled.AssembledMultiblockDefinition
import com.algorithmlx.ecr.api.multiblock.Multiblock
import com.algorithmlx.ecr.api.registries.ECRegistries
import com.algorithmlx.ecr.common.init.ECRModIDs
import com.algorithmlx.ecr.common.multiblocks.*
import com.algorithmlx.ecr.registry.MultiblockRegistry
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister

class NeoForgeMultiblockRegistry(bus: IEventBus): MultiblockRegistry {
    private val multiblocks = DeferredRegister.create(ECRegistries.MULTIBLOCK, ModId)
    private val assembled = DeferredRegister.create(ECRegistries.ASSEMBLED_MULTIBLOCK, ModId)

    init {
        multiblocks.register(bus)
        assembled.register(bus)
    }

    private val mithrilineFurnaceMultiblock = multiblocks.register(ECRModIDs.MITHRILINE_FURNACE) { _ -> MithrilineFurnaceMultiblock }
    private val soulStoneMultiblock = multiblocks.register(ECRModIDs.SOUL_STONE) { _ -> SoulStoneMultiblock }
    private val flameCrystalMultiblock = multiblocks.register(ECRModIDs.FLAME_CRYSTAL) { _ -> FlameCrystal }
    private val waterCrystalMultiblock = multiblocks.register(ECRModIDs.WATER_CRYSTAL) { _ -> WaterCrystal }
    private val earthCrystalMultiblock = multiblocks.register(ECRModIDs.EARTH_CRYSTAL) { _ -> EarthCrystal }
    private val airCrystalMultiblock = multiblocks.register(ECRModIDs.AIR_CRYSTAL) { _ -> AirCrystal }
    private val lightningCollectorMultiblock = multiblocks.register(ECRModIDs.LIGHTNING_COLLECTOR) { _ -> LightningCollector }
    private val enrichmentChamberMultiblock = multiblocks.register(ECRModIDs.ENRICHMENT_CHAMBER) { _ -> EnrichmentChamber }
    private val rayToweMultiblock = assembled.register(ECRModIDs.RAY_TOWER) { _ -> RayTowerMultiblock }
    private val magicalTeleporterMultiblock = multiblocks.register(ECRModIDs.MAGICAL_TELEPORTER) { _ -> MagicalTeleporter }

    override val mithrilineFurnace: Multiblock by lazy { mithrilineFurnaceMultiblock.get() }
    override val soulStone: Multiblock by lazy { soulStoneMultiblock.get() }
    override val flameCrystal: Multiblock by lazy { flameCrystalMultiblock.get() }
    override val waterCrystal: Multiblock by lazy { waterCrystalMultiblock.get() }
    override val earthCrystal: Multiblock by lazy { earthCrystalMultiblock.get() }
    override val airCrystal: Multiblock by lazy { airCrystalMultiblock.get() }
    override val lightningCollector: Multiblock by lazy { lightningCollectorMultiblock.get() }
    override val enrichmentChamber: Multiblock by lazy { enrichmentChamberMultiblock.get() }
    override val rayTower: AssembledMultiblockDefinition by lazy { rayToweMultiblock.get() }
    override val magicalTeleporter: Multiblock by lazy { magicalTeleporterMultiblock.get() }
}
