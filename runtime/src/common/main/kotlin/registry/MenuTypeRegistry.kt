package com.algorithmlx.ecr.registry

import com.algorithmlx.ecr.common.menu.*
import net.minecraft.world.inventory.MenuType

expect object MenuTypeRegistry {
    val mithrilineFurnace: MenuType<MithrilineFurnaceMenu>
    val magicTable: MenuType<MagicTableMenu>
    val matrixDestructor: MenuType<MatrixDestructorMenu>
}
