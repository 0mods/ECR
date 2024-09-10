package team._0mods.ecr.common.init.registry

import team._0mods.ecr.api.multiblock.IMultiblock
import team._0mods.ecr.api.utils.ecRL

object ECMultiblocks {
    val mithrilineFurnace: IMultiblock = IMultiblock.getFromJson("mithriline_furnace".ecRL)
    val soulStone: IMultiblock = IMultiblock.getFromJson("soul_stone".ecRL)
}
