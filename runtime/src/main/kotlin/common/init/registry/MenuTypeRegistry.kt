package com.algorithmlx.ecr.common.init.registry

import com.algorithmlx.ecr.common.menu.*
import net.minecraft.world.inventory.MenuType

interface MenuTypeRegistry {
    val mithrilineFurnace: MenuType<MithrilineFurnaceMenu>
    val envoyer: MenuType<EnvoyerMenu>
    val matrixDestructor: MenuType<MatrixDestructorMenu>

    companion object {
        lateinit var instance: MenuTypeRegistry
    }
}
