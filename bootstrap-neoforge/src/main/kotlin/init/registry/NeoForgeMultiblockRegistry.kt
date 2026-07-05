package com.algorithmlx.ecr.neoforge.init.registry

import com.algorithmlx.ecr.api.ModId
import com.algorithmlx.ecr.api.multiblock.Multiblock
import com.algorithmlx.ecr.api.registries.ECRegistries
import com.algorithmlx.ecr.common.init.registry.MultiblockRegistry
import com.algorithmlx.ecr.common.multiblocks.MithrilineFurnaceMultiblock
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister

class NeoForgeMultiblockRegistry(bus: IEventBus): MultiblockRegistry {
    private val multiblocks = DeferredRegister.create(ECRegistries.MULTIBLOCK, ModId)

    init {
        multiblocks.register(bus)
    }

    private val mithrilineFurnaceMultiblock = multiblocks.register("mithriline_furnace") { _ -> MithrilineFurnaceMultiblock }

    override val mithrilineFurnace: Multiblock by lazy { mithrilineFurnaceMultiblock.get() }
}
