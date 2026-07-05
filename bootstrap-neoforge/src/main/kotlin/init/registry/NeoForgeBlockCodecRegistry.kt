package com.algorithmlx.ecr.neoforge.init.registry

import com.algorithmlx.ecr.api.ModId
import com.algorithmlx.ecr.common.api.ClusterBlock
import com.algorithmlx.ecr.common.block.CrystalBlock
import com.algorithmlx.ecr.common.block.MithrilineFurnace
import com.algorithmlx.ecr.common.block.SolarPrism
import com.algorithmlx.ecr.common.init.registry.BlockCodecRegistry
import com.mojang.serialization.MapCodec
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.level.block.state.BlockBehaviour
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister

class NeoForgeBlockCodecRegistry(bus: IEventBus): BlockCodecRegistry {
    private val blockTypes = DeferredRegister.create(BuiltInRegistries.BLOCK_TYPE, ModId)

    init {
        blockTypes.register(bus)
    }

    private val solarPrismCodec = blockTypes.register("solar_prism") { _ -> BlockBehaviour.simpleCodec(::SolarPrism) }
    private val clusterBlockCodec = blockTypes.register("cluster") { _ -> BlockBehaviour.simpleCodec(::ClusterBlock) }
    private val crystalBlockCodec = blockTypes.register("crystal") { _ -> BlockBehaviour.simpleCodec(::CrystalBlock) }
    private val mithrilineFurnaceCodec = blockTypes.register("mithriline_furnace") { _ -> BlockBehaviour.simpleCodec(::MithrilineFurnace) }

    override val solarPrism: MapCodec<SolarPrism> by lazy { solarPrismCodec.get() }
    override val clusterBlock: MapCodec<ClusterBlock> by lazy { clusterBlockCodec.get() }
    override val crystalBlock: MapCodec<CrystalBlock> by lazy { crystalBlockCodec.get() }
    override val mithrilineFurnace: MapCodec<MithrilineFurnace> by lazy { mithrilineFurnaceCodec.get() }
}
