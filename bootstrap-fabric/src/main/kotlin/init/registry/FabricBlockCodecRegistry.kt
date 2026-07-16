package com.algorithmlx.ecr.fabric.init.registry

import com.algorithmlx.ecr.api.ecRL
import com.algorithmlx.ecr.common.api.block.ClusterBlock
import com.algorithmlx.ecr.common.block.CrystalBlock
import com.algorithmlx.ecr.common.block.Envoyer
import com.algorithmlx.ecr.common.block.MithrilineFurnace
import com.algorithmlx.ecr.common.block.SolarPrism
import com.algorithmlx.ecr.common.init.ECRModIDs
import com.algorithmlx.ecr.common.init.registry.BlockCodecRegistry
import com.mojang.serialization.MapCodec
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour

object FabricBlockCodecRegistry: BlockCodecRegistry {
    override val solarPrism: MapCodec<SolarPrism> = register(ECRModIDs.SOLAR_PRISM, BlockBehaviour.simpleCodec(::SolarPrism))
    override val clusterBlock: MapCodec<ClusterBlock> = register(ECRModIDs.CLUSTER, BlockBehaviour.simpleCodec(::ClusterBlock))
    override val crystalBlock: MapCodec<CrystalBlock> = register(ECRModIDs.CRYSTAL, BlockBehaviour.simpleCodec(::CrystalBlock))
    override val mithrilineFurnace: MapCodec<MithrilineFurnace> = register(ECRModIDs.MITHRILINE_FURNACE, BlockBehaviour.simpleCodec(::MithrilineFurnace))
    override val envoyer: MapCodec<Envoyer> = register(ECRModIDs.ENVOYER, BlockBehaviour.simpleCodec(::Envoyer))

    private fun <B: Block> register(id: String, codec: MapCodec<B>) =
        Registry.register(BuiltInRegistries.BLOCK_TYPE, id.ecRL, codec)
}
