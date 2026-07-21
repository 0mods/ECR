package com.algorithmlx.ecr.registry

import com.algorithmlx.ecr.api.ModId
import com.algorithmlx.ecr.api.multiblock.Multiblock
import com.algorithmlx.ecr.api.registries.ECRegistries
import com.algorithmlx.ecr.common.init.ECRModIDs
import com.algorithmlx.ecr.common.multiblocks.AirCrystal
import com.algorithmlx.ecr.common.multiblocks.EarthCrystal
import com.algorithmlx.ecr.common.multiblocks.FlameCrystal
import com.algorithmlx.ecr.common.multiblocks.LightningCollector
import com.algorithmlx.ecr.common.multiblocks.MithrilineFurnaceMultiblock
import com.algorithmlx.ecr.common.multiblocks.SoulStoneMultiblock
import com.algorithmlx.ecr.common.multiblocks.WaterCrystal
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister

@Suppress("ACTUAL_WITHOUT_EXPECT", "unused")
actual object MultiblockRegistry {
    private val multiblocks = DeferredRegister.create(ECRegistries.MULTIBLOCK, ModId)

    fun init(bus: IEventBus) {
        multiblocks.register(bus)
    }

    private val mithrilineFurnaceMultiblock = multiblocks.register(ECRModIDs.MITHRILINE_FURNACE) { _ -> MithrilineFurnaceMultiblock }
    private val soulStoneMultiblock = multiblocks.register(ECRModIDs.SOUL_STONE) { _ -> SoulStoneMultiblock }
    private val flameCrystalMultiblock = multiblocks.register(ECRModIDs.FLAME_CRYSTAL) { _ -> FlameCrystal }
    private val waterCrystalMultiblock = multiblocks.register(ECRModIDs.WATER_CRYSTAL) { _ -> WaterCrystal }
    private val earthCrystalMultiblock = multiblocks.register(ECRModIDs.EARTH_CRYSTAL) { _ -> EarthCrystal }
    private val airCrystalMultiblock = multiblocks.register(ECRModIDs.AIR_CRYSTAL) { _ -> AirCrystal }
    private val lightningCollectorMultiblock = multiblocks.register(ECRModIDs.LIGHTNING_COLLECTOR) { _ -> LightningCollector }

    actual val mithrilineFurnace: Multiblock by lazy { mithrilineFurnaceMultiblock.get() }
    actual val soulStone: Multiblock by lazy { soulStoneMultiblock.get() }
    actual val flameCrystal: Multiblock by lazy { flameCrystalMultiblock.get() }
    actual val waterCrystal: Multiblock by lazy { waterCrystalMultiblock.get() }
    actual val earthCrystal: Multiblock by lazy { earthCrystalMultiblock.get() }
    actual val airCrystal: Multiblock by lazy { airCrystalMultiblock.get() }
    actual val lightningCollector: Multiblock by lazy { lightningCollectorMultiblock.get() }
}
