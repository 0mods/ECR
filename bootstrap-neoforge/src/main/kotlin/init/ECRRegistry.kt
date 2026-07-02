package com.algorithmlx.ecr.neoforge.init

import com.algorithmlx.ecr.api.ModId
import com.algorithmlx.ecr.common.api.ClusterBlock
import com.algorithmlx.ecr.common.blocks.SolarPrism
import com.algorithmlx.ecr.common.init.Registry
import com.mojang.serialization.MapCodec
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.level.block.state.BlockBehaviour
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister

class ECRRegistry(bus: IEventBus): Registry {
    private val blockTypeRegistry = DeferredRegister.create(BuiltInRegistries.BLOCK_TYPE, ModId)
    private val blockRegistry = DeferredRegister.createBlocks(ModId)
    private val itemRegistry = DeferredRegister.createItems(ModId)

    init {
        blockTypeRegistry.register(bus)
        blockRegistry.register(bus)
        itemRegistry.register(bus)
    }

    val solarPrismType = blockTypeRegistry.register("solar_prism") { _ ->
        BlockBehaviour.simpleCodec(::SolarPrism)
    }

    val clusterType = blockTypeRegistry.register("cluster") { _ ->
        BlockBehaviour.simpleCodec(::ClusterBlock)
    }

    override val solarPrismCodec: MapCodec<SolarPrism> by lazy { solarPrismType.get() }
    override val clusterBlockCodec: MapCodec<ClusterBlock> by lazy { clusterType.get() }
}
