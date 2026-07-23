package com.algorithmlx.ecr.common.multiblocks

import com.algorithmlx.ecr.api.multiblock.Multiblock
import com.algorithmlx.ecr.api.multiblock.MultiblockMatcher
import com.algorithmlx.ecr.common.init.ECTags
import com.algorithmlx.ecr.registry.BlockRegistry
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

object FlameCrystal: Multiblock(3, 3, 1, {
    this.makeRecipeMB(
        this.block(Blocks.LAVA.defaultBlockState()),
        this.tag(BlockTags.INFINIBURN_NETHER)
    )
})

object WaterCrystal: Multiblock(3, 3, 1, {
    this.makeRecipeMB(
        this.block(Blocks.WATER.defaultBlockState()),
        this.tag(BlockTags.ICE)
    )
})

object EarthCrystal: Multiblock(3, 3, 1, {
    this.makeRecipeMB(
        this.block(Blocks.MOSSY_COBBLESTONE.defaultBlockState()),
        this.block(Blocks.MOSS_BLOCK.defaultBlockState())
    )
})

object AirCrystal: Multiblock(3, 3, 1, {
    this.makeRecipeMB(
        this.block(Blocks.END_STONE_BRICKS.defaultBlockState()),
        this.block(Blocks.PURPUR_BLOCK.defaultBlockState())
    )
})

object LightningCollector: Multiblock(11, 11, 4, {
    val cutCopperSlabList = Blocks.CUT_COPPER_SLAB.asList().map { this.block(it.defaultBlockState()) }
    val copperBlockList = Blocks.COPPER_BLOCK.asList().map { this.block(it.defaultBlockState()) }
    val lightningRodList = Blocks.LIGHTNING_ROD.asList().map { this.block(it.defaultBlockState()) }

    val voidStone = this.block(BlockRegistry.instance.voidStone.defaultBlockState())
    val mithrilinePlating = this.block(BlockRegistry.instance.mithrilinePlating.defaultBlockState())
    val copperSlabs = this.list(cutCopperSlabList)
    val copperBlocks = this.list(copperBlockList)
    val center = this.block(BlockRegistry.instance.mithrilineFurnace.defaultBlockState())
    val mithrilineCrystal = this.block(BlockRegistry.instance.mithrilineCrystal.defaultBlockState())
    val lightningRod = this.list(lightningRodList)

    pattern(
        null, null, null, voidStone, voidStone, null, voidStone, voidStone, null, null, null,
        null, mithrilinePlating, mithrilinePlating, null, null, voidStone, null, null, mithrilinePlating, mithrilinePlating, null,
        null, mithrilinePlating, null, null, voidStone, null, voidStone, null, null, mithrilinePlating, null,
        voidStone, null, null, voidStone, null, voidStone, null, voidStone, null, null, voidStone,
        voidStone, null, voidStone, null, null, null, null, null, voidStone, null, voidStone,
        null, voidStone, null, voidStone, null, null, null, voidStone, null, voidStone, null,
        voidStone, null, voidStone, null, null, null, null, null, voidStone, null, voidStone,
        voidStone, null, null, voidStone, null, voidStone, null, voidStone, null, null, voidStone,
        null, mithrilinePlating, null, null, voidStone, null, voidStone, null, null, mithrilinePlating, null,
        null, mithrilinePlating, mithrilinePlating, null, null, voidStone, null, null, mithrilinePlating, mithrilinePlating, null,
        null, null, null, voidStone, voidStone, null, voidStone, voidStone, null, null, null,

        null, null, null, null, null, null, null, null, null, null, null,
        null, mithrilinePlating, null, null, null, null, null, null, null, mithrilinePlating, null,
        null, null, null, null, null, null, null, null, null, null, null,
        null, null, null, null, copperSlabs, null, copperSlabs, null, null, null, null,
        null, null, null, copperSlabs, copperBlocks, voidStone, copperBlocks, copperSlabs, null, null, null,
        null, null, null, null, voidStone, null, voidStone, null, null, null, null,
        null, null, null, copperSlabs, copperBlocks, voidStone, copperBlocks, copperSlabs, null, null, null,
        null, null, null, null, copperSlabs, null, copperSlabs, null, null, null, null,
        null, null, null, null, null, null, null, null, null, null, null,
        null, mithrilinePlating, null, null, null, null, null, null, null, mithrilinePlating, null,
        null, null, null, null, null, null, null, null, null, null, null,

        null, null, null, null, null, null, null, null, null, null, null,
        null, null, null, null, null, null, null, null, null, null, null,
        null, null, null, null, null, null, null, null, null, null, null,
        null, null, null, null, null, null, null, null, null, null, null,
        null, null, null, null, null, null, null, null, null, null, null,
        null, null, null, null, null, center, null, null, null, null, null,
        null, null, null, null, null, null, null, null, null, null, null,
        null, null, null, null, null, null, null, null, null, null, null,
        null, null, null, null, null, null, null, null, null, null, null,
        null, null, null, null, null, null, null, null, null, null, null,
        null, null, null, null, null, null, null, null, null, null, null,

        null, null, null, null, null, null, null, null, null, null, null,
        null, mithrilineCrystal, null, null, null, null, null, null, null, mithrilineCrystal, null,
        null, null, null, null, null, null, null, null, null, null, null,
        null, null, null, null, null, null, null, null, null, null, null,
        null, null, null, null, null, null, null, null, null, null, null,
        null, null, null, null, null, lightningRod, null, null, null, null, null,
        null, null, null, null, null, null, null, null, null, null, null,
        null, null, null, null, null, null, null, null, null, null, null,
        null, null, null, null, null, null, null, null, null, null, null,
        null, mithrilineCrystal, null, null, null, null, null, null, null, mithrilineCrystal, null,
        null, null, null, null, null, null, null, null, null, null, null,
    )
})

object EnrichmentChamber: Multiblock(128, 128, 128, {
    val frame = this.tag(ECTags.Blocks.ENRICHMENT_CHAMBER)
    val air = this.block(Blocks.AIR.defaultBlockState())
    this.scalablePattern(2 ..< 64) {
        when {
            isBoundary -> frame
            else -> air
        }
    }
})

private fun Multiblock.makeRecipeMB(left: MultiblockMatcher, center: MultiblockMatcher) {
    pattern(
        null, left, null,
        left, center, left,
        null, left, null
    )
}
