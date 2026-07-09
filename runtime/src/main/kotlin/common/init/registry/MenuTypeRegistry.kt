package com.algorithmlx.ecr.common.init.registry

import com.algorithmlx.ecr.common.menu.EnvoyerMenu
import com.algorithmlx.ecr.common.menu.MithrilineFurnaceMenu
import net.minecraft.world.inventory.MenuType

interface MenuTypeRegistry {
    val mithrilineFurnace: MenuType<MithrilineFurnaceMenu>
    val envoyer: MenuType<EnvoyerMenu>

    companion object {
        lateinit var instance: MenuTypeRegistry
    }
}
