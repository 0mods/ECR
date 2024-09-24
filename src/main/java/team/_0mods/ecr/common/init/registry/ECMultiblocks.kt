package team._0mods.ecr.common.init.registry

import net.minecraft.tags.BlockTags
import net.minecraft.world.level.block.Blocks
import ru.hollowhorizon.hc.common.multiblock.Multiblock
import team._0mods.ecr.api.registries.register

object ECMultiblocks {
    //val mithrilineFurnace: IMultiblock = IMultiblock.getFromJson("mithriline_furnace".ecRL)
    //val soulStone: IMultiblock = IMultiblock.getFromJson("soul_stone".ecRL)
    val mithrilineFurnace = Multiblock {
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

    val soulStone = Multiblock {
        size(3, 3, 1)

        val a = tag(BlockTags.SOUL_SPEED_BLOCKS)
        val b = block(Blocks.EMERALD_BLOCK.defaultBlockState())

        pattern(
            null, a, null,
            a, b, a,
            null, a, null
        )
    }

    fun init() {
        mithrilineFurnace.register("mithriline_furnace")
        soulStone.register("soul_stone")
    }
}
