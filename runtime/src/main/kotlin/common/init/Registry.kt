package com.algorithmlx.ecr.common.init

import com.algorithmlx.ecr.common.api.ClusterBlock
import com.algorithmlx.ecr.common.blocks.CrystalBlock
import com.algorithmlx.ecr.common.blocks.SolarPrism
import com.algorithmlx.ecr.common.components.SoulStoneComponent
import com.algorithmlx.ecr.common.items.SoulStone
import com.mojang.serialization.MapCodec
import net.minecraft.core.component.DataComponentType

interface Registry {
    val solarPrismCodec: MapCodec<SolarPrism>
    val clusterBlockCodec: MapCodec<ClusterBlock>
    val crystalBlockCodec: MapCodec<CrystalBlock>

    val soulStone: SoulStone

    val soulStoneComponent: DataComponentType<SoulStoneComponent>

    companion object { @JvmStatic lateinit var instance: Registry }
}
