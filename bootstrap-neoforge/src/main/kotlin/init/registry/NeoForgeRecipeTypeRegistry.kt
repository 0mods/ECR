package com.algorithmlx.ecr.neoforge.init.registry

import com.algorithmlx.ecr.api.ModId
import com.algorithmlx.ecr.common.init.registry.RecipeTypeRegistry
import com.algorithmlx.ecr.common.recipe.MithrilineFurnaceRecipe
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.crafting.RecipeType
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister

class NeoForgeRecipeTypeRegistry(bus: IEventBus): RecipeTypeRegistry {
    private val recipeTypes = DeferredRegister.create(BuiltInRegistries.RECIPE_TYPE, ModId)

    init {
        recipeTypes.register(bus)
    }

    private val mithrilineFurnaceType = recipeTypes.register("mithriline_furnace") { rk ->
        RecipeType.simple<MithrilineFurnaceRecipe>(rk)
    }

    override val mithrilineFurnace: RecipeType<MithrilineFurnaceRecipe> by lazy { mithrilineFurnaceType.get() }
}