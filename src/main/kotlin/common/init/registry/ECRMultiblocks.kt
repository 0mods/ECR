package team._0mods.ecr.common.init.registry

import net.minecraft.tags.BlockTags
import net.minecraft.world.level.block.Blocks
import ru.hollowhorizon.hc.common.multiblock.Multiblock
import ru.hollowhorizon.hc.common.registry.HollowRegistry
import team._0mods.ecr.api.ModId

object ECRMultiblocks : HollowRegistry(ModId) {
    val mithrilineFurnace by register("mithriline_furnace") {
        Multiblock {
            /*size(5, 5, 3)
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
            )*/
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

    private fun Multiblock.makeRecipeMB(left: Multiblock.Matcher, center: Multiblock.Matcher) {
        this.size(3, 3, 1)

        /*pattern(
            null, left, null,
            left, center, left,
            null, left, null
        )*/
    }
}
