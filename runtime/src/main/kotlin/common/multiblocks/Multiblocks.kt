package com.algorithmlx.ecr.common.multiblocks

import com.algorithmlx.ecr.api.multiblock.Multiblock
import com.algorithmlx.ecr.api.multiblock.MultiblockMatcher
import com.algorithmlx.ecr.common.init.registry.BlockRegistry
import net.minecraft.tags.BlockTags
import net.minecraft.world.level.block.Blocks

object MithrilineFurnaceMultiblock: Multiblock(5, 5, 3, {
    val a = block(BlockRegistry.instance.mithrilinePlating.defaultBlockState())
    val b = block(BlockRegistry.instance.mithrilineFurnace.defaultBlockState())

    pattern(
        a, null, a, null, a,
        null, a, a, a, null,
        a, a, null, a, a,
        null, a, a, a, null,
        a, null, a, null, a,

        a, null, a, null, a,
        null, null, null, null, null,
        a, null, b, null, a,
        null, null, null, null, null,
        a, null, a, null, a,

        a, null, null, null, a,
        null, null, null, null, null,
        null, null, null, null, null,
        null, null, null, null, null,
        a, null, null, null, a,
    )
})

object SoulStoneMultiblock: Multiblock(3, 3, 1, {
    this.makeRecipeMB(
        this.tag(BlockTags.SOUL_SPEED_BLOCKS),
        this.block(Blocks.EMERALD_BLOCK.defaultBlockState())
    )
})

private fun Multiblock.makeRecipeMB(left: MultiblockMatcher, center: MultiblockMatcher) {
    pattern(
        null, left, null,
        left, center, left,
        null, left, null
    )
}
