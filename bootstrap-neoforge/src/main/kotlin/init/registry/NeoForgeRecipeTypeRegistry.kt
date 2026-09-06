package com.algorithmlx.ecr.neoforge.init.registry

import com.algorithmlx.ecr.api.ModId
import com.algorithmlx.ecr.common.init.ECRModIDs
import com.algorithmlx.ecr.common.recipe.MagicTableRecipe
import com.algorithmlx.ecr.common.recipe.StructureRecipe
import com.algorithmlx.ecr.common.recipe.MithrilineFurnaceRecipe
import com.algorithmlx.ecr.common.recipe.RadiatingChamberRecipe
import com.algorithmlx.ecr.registry.RecipeTypeRegistry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeType
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister

class NeoForgeRecipeTypeRegistry(bus: IEventBus): RecipeTypeRegistry {
    private val recipeTypes = DeferredRegister.create(BuiltInRegistries.RECIPE_TYPE, ModId)

    init {
        recipeTypes.register(bus)
    }

    override val mithrilineFurnace: RecipeType<MithrilineFurnaceRecipe> by register(ECRModIDs.MITHRILINE_FURNACE)
    override val radiatingChamber: RecipeType<RadiatingChamberRecipe> by register(ECRModIDs.RADIATING_CHAMBER)
    override val structure: RecipeType<StructureRecipe> by register(ECRModIDs.STRUCTURE)
    override val magicTable: RecipeType<MagicTableRecipe> by register(ECRModIDs.MAGIC_TABLE)

    private fun <T: Recipe<*>> register(id: String) = recipeTypes.register(id) { registeredId -> RecipeType.simple<T>(registeredId) }
}
