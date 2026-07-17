package com.algorithmlx.ecr.common.init

import com.algorithmlx.ecr.api.utils.ecRL
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.crafting.RecipePropertySet

object ResourceKeys {
    val mithrilineFurnaceKey = registerPropertySet("mithriline_furnace")

    private fun registerPropertySet(name: String) = ResourceKey.create(RecipePropertySet.TYPE_KEY, name.ecRL)
}