package com.algorithmlx.ecr.common.init

import net.minecraft.util.valueproviders.UniformInt
import net.minecraft.world.level.block.state.BlockBehaviour

object ECRBlockProperties {
    // patterns
    val clusters = properties { it.strength(1.5F).requiresCorrectToolForDrops() }
    val witherProtected = properties { it.explosionResistance(3600000.0F).requiresCorrectToolForDrops() }
    // properties
    @JvmField val DEFAULTS = BlockBehaviour.Properties.of().strength(1.5F, 6F)

    @JvmField val MECHANISM = BlockBehaviour.Properties.of().strength(5F, 1200F).requiresCorrectToolForDrops()
    @JvmField val FORTIFIED_GLASS = BlockBehaviour.Properties.of().noOcclusion().strength(5F).requiresCorrectToolForDrops()
    @JvmField val FORTIFIED_STONE = BlockBehaviour.Properties.of().strength(5F).requiresCorrectToolForDrops()
    @JvmField val MITHRILINE_ORE = BlockBehaviour.Properties.of().strength(4.5F, 3F).requiresCorrectToolForDrops()

    // Experience
    @JvmField val MITHRILINE_ORE_EXPERIENCE = UniformInt.of(0, 5)

    private fun properties(property: (BlockBehaviour.Properties) -> BlockBehaviour.Properties) = property
}
