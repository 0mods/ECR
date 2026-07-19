package com.algorithmlx.ecr.common.init

import com.algorithmlx.ecr.api.utils.ecRL
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.crafting.RecipePropertySet

object ResourceKeys {
    @JvmStatic
    val MRU_DAMAGE_TYPE = registerDamageType(ECRModIDs.MRU)

    private fun registerDamageType(name: String) = ResourceKey.create(Registries.DAMAGE_TYPE, name.ecRL)
}
