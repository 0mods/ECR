package com.algorithmlx.ecr.fabric.init.registry

import com.algorithmlx.ecr.api.ecRL
import com.algorithmlx.ecr.api.multiblock.Multiblock
import com.algorithmlx.ecr.api.registries.ECRegistries
import com.algorithmlx.ecr.common.init.ECRModIDs
import com.algorithmlx.ecr.common.init.registry.MultiblockRegistry
import com.algorithmlx.ecr.common.multiblocks.MithrilineFurnaceMultiblock
import com.algorithmlx.ecr.common.multiblocks.SoulStoneMultiblock
import net.minecraft.core.Registry

object FabricMultiblockRegistry: MultiblockRegistry {
    override val mithrilineFurnace: Multiblock = register(ECRModIDs.MITHRILINE_FURNACE, MithrilineFurnaceMultiblock)
    override val soulStone: Multiblock = register(ECRModIDs.SOUL_STONE, SoulStoneMultiblock)

    private fun <T: Multiblock> register(id: String, multiblock: T) = Registry.register(
        ECRegistries.MULTIBLOCK, id.ecRL, multiblock
    )
}
