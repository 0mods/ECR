package com.algorithmlx.ecr.neoforge.init.registry

import com.algorithmlx.ecr.api.ModId
import com.algorithmlx.ecr.common.init.ECRModIDs
import com.algorithmlx.ecr.neoforge.api.CountIngredient
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.common.crafting.IngredientType
import net.neoforged.neoforge.registries.DeferredRegister
import net.neoforged.neoforge.registries.NeoForgeRegistries

object IngredientRegistry {
    private val registry = DeferredRegister.create(NeoForgeRegistries.INGREDIENT_TYPES, ModId)

    fun init(bus: IEventBus) {
        registry.register(bus)
    }

    @JvmField
    val COUNT_TYPE = registry.register(ECRModIDs.COUNT) { _ -> IngredientType(CountIngredient.CODEC, CountIngredient.STREAM_CODEC) }
}
