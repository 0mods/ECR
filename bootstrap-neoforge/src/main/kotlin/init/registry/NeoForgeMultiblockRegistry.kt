package com.algorithmlx.ecr.neoforge.init.registry

import com.algorithmlx.ecr.api.ModId
import com.algorithmlx.ecr.api.multiblock.Multiblock
import com.algorithmlx.ecr.api.registries.ECRegistries
import com.algorithmlx.ecr.common.init.ECRModIDs
import com.algorithmlx.ecr.common.init.registry.MultiblockRegistry
import com.algorithmlx.ecr.common.multiblocks.MithrilineFurnaceMultiblock
import com.algorithmlx.ecr.common.multiblocks.SoulStoneMultiblock
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister

class NeoForgeMultiblockRegistry(bus: IEventBus): MultiblockRegistry {
    private val multiblocks = DeferredRegister.create(ECRegistries.MULTIBLOCK, ModId)

    init {
        multiblocks.register(bus)
    }

    private val mithrilineFurnaceMultiblock = multiblocks.register(ECRModIDs.MITHRILINE_FURNACE) { _ -> MithrilineFurnaceMultiblock }
    private val soulStoneMultiblock = multiblocks.register(ECRModIDs.SOUL_STONE) { _ -> SoulStoneMultiblock }

    override val mithrilineFurnace: Multiblock by lazy { mithrilineFurnaceMultiblock.get() }
    override val soulStone: Multiblock by lazy { soulStoneMultiblock.get() }
}
