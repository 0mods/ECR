package team._0mods.ecr.common.init.registry

import team._0mods.ecr.ModId
import team._0mods.ecr.api.multiblock.IMultiblock

object ECMultiblocks {
    val mithrilineFurnace: IMultiblock = IMultiblock.getFromJson("$ModId:mithriline_furnace")
    val soulStone: IMultiblock = IMultiblock.getFromJson("$ModId:soul_stone")
}