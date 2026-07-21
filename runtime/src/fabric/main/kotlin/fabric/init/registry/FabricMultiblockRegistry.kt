package com.algorithmlx.ecr.fabric.init.registry

import com.algorithmlx.ecr.api.utils.ecRL
import com.algorithmlx.ecr.api.multiblock.Multiblock
import com.algorithmlx.ecr.api.registries.ECRegistries
import com.algorithmlx.ecr.common.init.ECRModIDs
import com.algorithmlx.ecr.registry.MultiblockRegistry
import com.algorithmlx.ecr.common.multiblocks.AirCrystal
import com.algorithmlx.ecr.common.multiblocks.EarthCrystal
import com.algorithmlx.ecr.common.multiblocks.FlameCrystal
import com.algorithmlx.ecr.common.multiblocks.LightningCollector
import com.algorithmlx.ecr.common.multiblocks.MithrilineFurnaceMultiblock
import com.algorithmlx.ecr.common.multiblocks.SoulStoneMultiblock
import com.algorithmlx.ecr.common.multiblocks.WaterCrystal
import net.minecraft.core.Registry

object FabricMultiblockRegistry: MultiblockRegistry {
    override val mithrilineFurnace: Multiblock = register(ECRModIDs.MITHRILINE_FURNACE, MithrilineFurnaceMultiblock)
    override val soulStone: Multiblock = register(ECRModIDs.SOUL_STONE, SoulStoneMultiblock)
    override val flameCrystal: Multiblock = register(ECRModIDs.FLAME_CRYSTAL, FlameCrystal)
    override val waterCrystal: Multiblock = register(ECRModIDs.WATER_CRYSTAL, WaterCrystal)
    override val earthCrystal: Multiblock = register(ECRModIDs.EARTH_CRYSTAL, EarthCrystal)
    override val airCrystal: Multiblock = register(ECRModIDs.AIR_CRYSTAL, AirCrystal)
    override val lightningCollector: Multiblock = register(ECRModIDs.LIGHTNING_COLLECTOR, LightningCollector)

    private fun <T: Multiblock> register(id: String, multiblock: T) = Registry.register(
        ECRegistries.MULTIBLOCK, id.ecRL, multiblock
    )
}
