package com.algorithmlx.ecr.neoforge.init.registry

import com.algorithmlx.ecr.api.ModId
import com.algorithmlx.ecr.common.init.ECRModIDs
import com.algorithmlx.ecr.common.recipe.MagicTableRecipe
import com.algorithmlx.ecr.common.recipe.StructureRecipe
import com.algorithmlx.ecr.common.recipe.MithrilineFurnaceRecipe
import com.algorithmlx.ecr.registry.RecipeTypeRegistry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.crafting.RecipeType
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister

class NeoForgeRecipeTypeRegistry(bus: IEventBus): RecipeTypeRegistry {
    private val recipeTypes = DeferredRegister.create(BuiltInRegistries.RECIPE_TYPE, ModId)

    init {
        recipeTypes.register(bus)
    }

    private val mithrilineFurnaceType = recipeTypes.register(ECRModIDs.MITHRILINE_FURNACE) { rk ->
        RecipeType.simple<MithrilineFurnaceRecipe>(rk)
    }
    private val structureRecipe = recipeTypes.register(ECRModIDs.STRUCTURE) { rk ->
        RecipeType.simple<StructureRecipe>(rk)
    }
    private val magicTableRecipe = recipeTypes.register(ECRModIDs.MAGIC_TABLE) { rk ->
        RecipeType.simple<MagicTableRecipe>(rk)
    }

    override val mithrilineFurnace: RecipeType<MithrilineFurnaceRecipe> by lazy { mithrilineFurnaceType.get() }
    override val structure: RecipeType<StructureRecipe> by lazy { structureRecipe.get() }
    override val magicTable: RecipeType<MagicTableRecipe> by lazy { magicTableRecipe.get() }
}
