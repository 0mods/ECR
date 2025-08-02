package team._0mods.ecr.common.init.registry

import net.minecraft.tags.BlockTags
import net.minecraft.world.level.block.Blocks
import ru.hollowhorizon.hc.common.multiblock.Multiblock
import ru.hollowhorizon.hc.common.registry.HollowRegistry
import team._0mods.ecr.api.ModId

object ECRMultiblocks : HollowRegistry(ModId) {
    val mithrilineFurnace by register("mithriline_furnace") {
        Multiblock {
            size(5, 5, 3)
            val a = block(ECRRegistry.mithrilinePlating.defaultBlockState())
            val b = block(ECRRegistry.mithrilineFurnace.defaultBlockState())

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
        }
    }

    val soulStone by register("soul_stone") {
        Multiblock {
            this.makeRecipeMB(this.tag(BlockTags.SOUL_SPEED_BLOCKS), this.block(Blocks.EMERALD_BLOCK.defaultBlockState()))
        }
    }

    val flameCrystal by register("flame_crystal") {
        Multiblock {
            this.makeRecipeMB(this.block(Blocks.LAVA.defaultBlockState()), this.tag(BlockTags.INFINIBURN_NETHER))
        }
    }

    val waterCrystal by register("water_crystal") {
        Multiblock {
            this.makeRecipeMB(this.block(Blocks.WATER.defaultBlockState()), this.tag(BlockTags.ICE))
        }
    }

    val earthCrystal by register("earth_crystal") {
        Multiblock {
            this.makeRecipeMB(this.block(Blocks.MOSSY_COBBLESTONE.defaultBlockState()), this.block(Blocks.MOSS_BLOCK.defaultBlockState()))
        }
    }

    val airCrystal by register("air_crystal") {
        Multiblock {
            this.makeRecipeMB(this.block(Blocks.END_STONE_BRICKS.defaultBlockState()), this.block(Blocks.PURPUR_BLOCK.defaultBlockState()))
        }
    }

    val lightningCollector by register("lightning_collector") {
        Multiblock {
            size(11, 11, 4)

            val voidStone = this.block(ECRRegistry.voidStone.defaultBlockState())
            val mithrilinePlating = this.block(ECRRegistry.mithrilinePlating.defaultBlockState())
            val copperSlabs = this.tag(ECTags.copperSlabs)
            val copperBlocks = this.tag(ECTags.copperBlocks)
            val center = this.block(ECRRegistry.mithrilineFurnace.defaultBlockState())
            val mithrilineCrystal = this.block(ECRRegistry.mithrilineCrystal.defaultBlockState())
            val lightningRod = this.block(Blocks.LIGHTNING_ROD.defaultBlockState())

            pattern(
                null, null, null, voidStone, voidStone, null, voidStone, voidStone, null, null, null,
                null, null, mithrilinePlating, null, null, voidStone, null, null, mithrilinePlating, null, null,
                null, mithrilinePlating, null, null, voidStone, null, voidStone, null, null, mithrilinePlating, null,
                voidStone, null, null, voidStone, null, voidStone, null, voidStone, null, null, voidStone,
                voidStone, null, voidStone, null, null, null, null, null, voidStone, null, voidStone,
                null, voidStone, null, voidStone, null, null, null, voidStone, null, voidStone, null,
                voidStone, null, voidStone, null, null, null, null, null, voidStone, null, voidStone,
                voidStone, null, null, voidStone, null, voidStone, null, voidStone, null, null, voidStone,
                null, mithrilinePlating, null, null, voidStone, null, voidStone, null, null, mithrilinePlating, null,
                null, null, mithrilinePlating, null, null, voidStone, null, null, mithrilinePlating, null, null,
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
        }
    }

    private fun Multiblock.makeRecipeMB(left: Multiblock.Matcher, center: Multiblock.Matcher) {
        this.size(3, 3, 1)

        pattern(
            null, left, null,
            left, center, left,
            null, left, null
        )
    }
}
