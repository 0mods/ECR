package com.algorithmlx.ecr.registry

import com.algorithmlx.ecr.api.utils.ecRL
import com.algorithmlx.ecr.common.api.block.ClusterBlock
import com.algorithmlx.ecr.common.block.ColdDistiller
import com.algorithmlx.ecr.common.block.CrystalBlock
import com.algorithmlx.ecr.common.block.MagicTable
import com.algorithmlx.ecr.common.block.MatrixDestructor
import com.algorithmlx.ecr.common.block.MithrilineFurnace
import com.algorithmlx.ecr.common.block.SolarPrism
import com.algorithmlx.ecr.common.init.ECRModIDs
import com.mojang.serialization.MapCodec
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour

@Suppress("ACTUAL_WITHOUT_EXPECT", "unused")
actual object BlockCodecRegistry {
    actual val solarPrism: MapCodec<SolarPrism> = register(ECRModIDs.SOLAR_PRISM, BlockBehaviour.simpleCodec(::SolarPrism))
    actual val clusterBlock: MapCodec<ClusterBlock> = register(ECRModIDs.CLUSTER, BlockBehaviour.simpleCodec(::ClusterBlock))
    actual val crystalBlock: MapCodec<CrystalBlock> = register(ECRModIDs.CRYSTAL, BlockBehaviour.simpleCodec(::CrystalBlock))
    actual val mithrilineFurnace: MapCodec<MithrilineFurnace> = register(ECRModIDs.MITHRILINE_FURNACE, BlockBehaviour.simpleCodec(::MithrilineFurnace))
    actual val magicTable: MapCodec<MagicTable> = register(ECRModIDs.MAGIC_TABLE, BlockBehaviour.simpleCodec(::MagicTable))
    actual val matrixDestructor: MapCodec<MatrixDestructor> = register(ECRModIDs.MATRIX_DESTRUCTOR, BlockBehaviour.simpleCodec(::MatrixDestructor))
    actual val coldDistiller: MapCodec<ColdDistiller> = register(ECRModIDs.COLD_DISTILLER, BlockBehaviour.simpleCodec(::ColdDistiller))

    private fun <B: Block> register(id: String, codec: MapCodec<B>) =
        Registry.register(BuiltInRegistries.BLOCK_TYPE, id.ecRL, codec)
}
