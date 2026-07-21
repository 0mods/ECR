package com.algorithmlx.ecr.registry

import com.algorithmlx.ecr.common.menu.*
import net.minecraft.world.inventory.MenuType

interface MenuTypeRegistry {
    val mithrilineFurnace: MenuType<MithrilineFurnaceMenu>
    val envoyer: MenuType<MagicTableMenu>
    val matrixDestructor: MenuType<MatrixDestructorMenu>

    companion object {
        lateinit var instance: MenuTypeRegistry
    }
}
