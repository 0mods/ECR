package com.algorithmlx.ecr.fabric.init.registry

import com.algorithmlx.ecr.api.ecRL
import com.algorithmlx.ecr.common.api.ClusterBlock
import com.algorithmlx.ecr.common.block.CrystalBlock
import com.algorithmlx.ecr.common.block.MithrilineFurnace
import com.algorithmlx.ecr.common.block.SolarPrism
import com.algorithmlx.ecr.common.init.registry.BlockCodecRegistry
import com.mojang.serialization.MapCodec
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour

object FabricBlockCodecRegistry: BlockCodecRegistry {
    override val solarPrism: MapCodec<SolarPrism> = register("solar_prism", BlockBehaviour.simpleCodec(::SolarPrism))
    override val clusterBlock: MapCodec<ClusterBlock> = register("cluster", BlockBehaviour.simpleCodec(::ClusterBlock))
    override val crystalBlock: MapCodec<CrystalBlock> = register("crystal", BlockBehaviour.simpleCodec(::CrystalBlock))
    override val mithrilineFurnace: MapCodec<MithrilineFurnace> = register("mithriline_furnace", BlockBehaviour.simpleCodec(::MithrilineFurnace))

    private fun <B: Block> register(id: String, codec: MapCodec<B>) =
        Registry.register(BuiltInRegistries.BLOCK_TYPE, id.ecRL, codec)
}
