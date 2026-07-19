package com.algorithmlx.ecr.neoforge.init.registry

import com.algorithmlx.ecr.api.ModId
import com.algorithmlx.ecr.common.api.block.ClusterBlock
import com.algorithmlx.ecr.common.block.ColdDistiller
import com.algorithmlx.ecr.common.block.CrystalBlock
import com.algorithmlx.ecr.common.block.MagicTable
import com.algorithmlx.ecr.common.block.MatrixDestructor
import com.algorithmlx.ecr.common.block.MithrilineFurnace
import com.algorithmlx.ecr.common.block.SolarPrism
import com.algorithmlx.ecr.common.init.ECRModIDs
import com.algorithmlx.ecr.common.init.registry.BlockCodecRegistry
import com.mojang.serialization.MapCodec
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.level.block.state.BlockBehaviour
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister

class NeoForgeBlockCodecRegistry(bus: IEventBus): BlockCodecRegistry {
    private val blockTypes = DeferredRegister.create(BuiltInRegistries.BLOCK_TYPE, ModId)

    init {
        blockTypes.register(bus)
    }

    private val solarPrismCodec = blockTypes.register(ECRModIDs.SOLAR_PRISM) { _ -> BlockBehaviour.simpleCodec(::SolarPrism) }
    private val clusterBlockCodec = blockTypes.register(ECRModIDs.CLUSTER) { _ -> BlockBehaviour.simpleCodec(::ClusterBlock) }
    private val crystalBlockCodec = blockTypes.register(ECRModIDs.CRYSTAL) { _ -> BlockBehaviour.simpleCodec(::CrystalBlock) }
    private val mithrilineFurnaceCodec = blockTypes.register(ECRModIDs.MITHRILINE_FURNACE) { _ -> BlockBehaviour.simpleCodec(::MithrilineFurnace) }
    private val magicTableCodec = blockTypes.register(ECRModIDs.MAGIC_TABLE) { _ -> BlockBehaviour.simpleCodec(::MagicTable) }
    private val matrixDestructorCodec = blockTypes.register(ECRModIDs.MATRIX_DESTRUCTOR) { _ -> BlockBehaviour.simpleCodec(::MatrixDestructor) }
    private val coldDistillerCodec = blockTypes.register(ECRModIDs.COLD_DISTILLER) { _ -> BlockBehaviour.simpleCodec(::ColdDistiller) }

    override val solarPrism: MapCodec<SolarPrism> by lazy { solarPrismCodec.get() }
    override val clusterBlock: MapCodec<ClusterBlock> by lazy { clusterBlockCodec.get() }
    override val crystalBlock: MapCodec<CrystalBlock> by lazy { crystalBlockCodec.get() }
    override val mithrilineFurnace: MapCodec<MithrilineFurnace> by lazy { mithrilineFurnaceCodec.get() }
    override val magicTable: MapCodec<MagicTable> by lazy { magicTableCodec.get() }
    override val matrixDestructor: MapCodec<MatrixDestructor> by lazy { matrixDestructorCodec.get() }
    override val coldDistiller: MapCodec<ColdDistiller> by lazy { coldDistillerCodec.get() }
}
