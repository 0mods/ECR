package com.algorithmlx.ecr.registry

import com.algorithmlx.ecr.api.ModId
import com.algorithmlx.ecr.common.api.block.ClusterBlock
import com.algorithmlx.ecr.common.block.*
import com.algorithmlx.ecr.common.init.ECRModIDs
import com.mojang.serialization.MapCodec
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.level.block.state.BlockBehaviour
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister

@Suppress("ACTUAL_WITHOUT_EXPECT", "unused")
actual object BlockCodecRegistry {
    private val blockTypes = DeferredRegister.create(BuiltInRegistries.BLOCK_TYPE, ModId)

    fun init(bus: IEventBus) {
        blockTypes.register(bus)
    }

    private val solarPrismCodec = blockTypes.register(ECRModIDs.SOLAR_PRISM) { _ -> BlockBehaviour.simpleCodec(::SolarPrism) }
    private val clusterBlockCodec = blockTypes.register(ECRModIDs.CLUSTER) { _ -> BlockBehaviour.simpleCodec(::ClusterBlock) }
    private val crystalBlockCodec = blockTypes.register(ECRModIDs.CRYSTAL) { _ -> BlockBehaviour.simpleCodec(::CrystalBlock) }
    private val mithrilineFurnaceCodec = blockTypes.register(ECRModIDs.MITHRILINE_FURNACE) { _ -> BlockBehaviour.simpleCodec(::MithrilineFurnace) }
    private val magicTableCodec = blockTypes.register(ECRModIDs.MAGIC_TABLE) { _ -> BlockBehaviour.simpleCodec(::MagicTable) }
    private val matrixDestructorCodec = blockTypes.register(ECRModIDs.MATRIX_DESTRUCTOR) { _ -> BlockBehaviour.simpleCodec(::MatrixDestructor) }
    private val coldDistillerCodec = blockTypes.register(ECRModIDs.COLD_DISTILLER) { _ -> BlockBehaviour.simpleCodec(::ColdDistiller) }

    actual val solarPrism: MapCodec<SolarPrism> by lazy { solarPrismCodec.get() }
    actual val clusterBlock: MapCodec<ClusterBlock> by lazy { clusterBlockCodec.get() }
    actual val crystalBlock: MapCodec<CrystalBlock> by lazy { crystalBlockCodec.get() }
    actual val mithrilineFurnace: MapCodec<MithrilineFurnace> by lazy { mithrilineFurnaceCodec.get() }
    actual val magicTable: MapCodec<MagicTable> by lazy { magicTableCodec.get() }
    actual val matrixDestructor: MapCodec<MatrixDestructor> by lazy { matrixDestructorCodec.get() }
    actual val coldDistiller: MapCodec<ColdDistiller> by lazy { coldDistillerCodec.get() }
}
