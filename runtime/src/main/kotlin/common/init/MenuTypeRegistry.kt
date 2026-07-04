package com.algorithmlx.ecr.common.init

import com.algorithmlx.ecr.common.menu.MithrilineFurnaceMenu
import net.minecraft.world.inventory.MenuType

interface MenuTypeRegistry {
    val mithrilineFurnaceMenu: MenuType<MithrilineFurnaceMenu>

    companion object {
        @JvmStatic
        val instance: MenuTypeRegistry = UnionRegistry.instance
    }
}
