package com.algorithmlx.ecr.registry

import com.algorithmlx.ecr.common.menu.*
import net.minecraft.world.inventory.MenuType

interface MenuTypeRegistry {
    val mithrilineFurnace: MenuType<MithrilineFurnaceMenu>
    val magicTable: MenuType<MagicTableMenu>
    val matrixDestructor: MenuType<MatrixDestructorMenu>
    val enrichmentChamberController: MenuType<EnrichmentChamberControllerMenu>
    val enrichmentChamberReceiver: MenuType<EnrichmentChamberReceiverMenu>
    val rayTower: MenuType<RayTowerMenu>

    companion object {
        @JvmStatic
        lateinit var instance: MenuTypeRegistry
    }
}
