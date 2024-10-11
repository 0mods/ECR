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
            val a = block(ECRegistry.mithrilinePlating.get().defaultBlockState())
            val b = block(ECRegistry.mithrilineFurnace.get().defaultBlockState())

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
            size(3, 3, 1)

            val a = tag(BlockTags.SOUL_SPEED_BLOCKS)
            val b = block(Blocks.EMERALD_BLOCK.defaultBlockState())

            pattern(
                null, a, null,
                a, b, a,
                null, a, null
            )
        }
    }
}
