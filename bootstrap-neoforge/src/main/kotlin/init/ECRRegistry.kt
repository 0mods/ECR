package com.algorithmlx.ecr.neoforge.init

import com.algorithmlx.ecr.api.ModId
import com.algorithmlx.ecr.common.api.ClusterBlock
import com.algorithmlx.ecr.common.blocks.CrystalBlock
import com.algorithmlx.ecr.common.blocks.SolarPrism
import com.algorithmlx.ecr.common.components.SoulStoneComponent
import com.algorithmlx.ecr.common.init.Registry
import com.algorithmlx.ecr.common.items.SoulStone
import com.mojang.serialization.MapCodec
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.world.level.block.state.BlockBehaviour
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister

class ECRRegistry(bus: IEventBus): Registry {
    private val blockTypeRegistry = DeferredRegister.create(BuiltInRegistries.BLOCK_TYPE, ModId)
    private val blockRegistry = DeferredRegister.createBlocks(ModId)
    private val itemRegistry = DeferredRegister.createItems(ModId)
    private val dataComponentRegistry = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, ModId)

    init {
        blockTypeRegistry.register(bus)
        blockRegistry.register(bus)
        itemRegistry.register(bus)
        dataComponentRegistry.register(bus)
    }

    // Block Types
    private val solarPrismType = blockTypeRegistry.register("solar_prism") { _ ->
        BlockBehaviour.simpleCodec(::SolarPrism)
    }
    private val clusterType = blockTypeRegistry.register("cluster") { _ ->
        BlockBehaviour.simpleCodec(::ClusterBlock)
    }
    private val crystalType = blockTypeRegistry.register("crystal") { _ ->
        BlockBehaviour.simpleCodec(::CrystalBlock)
    }

    // DataComponent
    val soulStoneComponentRegistry = dataComponentRegistry.registerComponentType("soul_stone") { builder ->
        builder.persistent(SoulStoneComponent.codec)
            .networkSynchronized(SoulStoneComponent.codecStream)
    }

    // Implements
    override val solarPrismCodec: MapCodec<SolarPrism> by lazy { solarPrismType.get() }
    override val clusterBlockCodec: MapCodec<ClusterBlock> by lazy { clusterType.get() }
    override val crystalBlockCodec: MapCodec<CrystalBlock> by lazy { crystalType.get() }
    override val soulStone: SoulStone
        get() = TODO("Not yet implemented")

    override val soulStoneComponent: DataComponentType<SoulStoneComponent> by lazy { soulStoneComponentRegistry.get() }
}
