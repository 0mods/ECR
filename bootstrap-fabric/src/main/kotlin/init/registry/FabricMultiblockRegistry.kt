package com.algorithmlx.ecr.fabric.init.registry

import com.algorithmlx.ecr.api.ecRL
import com.algorithmlx.ecr.api.multiblock.Multiblock
import com.algorithmlx.ecr.api.registries.ECRegistries
import com.algorithmlx.ecr.common.init.registry.MultiblockRegistry
import com.algorithmlx.ecr.common.multiblocks.MithrilineFurnaceMultiblock
import net.minecraft.core.Registry

object FabricMultiblockRegistry: MultiblockRegistry {
    override val mithrilineFurnace: Multiblock = register("mithriline_furnace", MithrilineFurnaceMultiblock)

    private fun <T: Multiblock> register(id: String, multiblock: T) = Registry.register(ECRegistries.MULTIBLOCK, id.ecRL, multiblock)
}