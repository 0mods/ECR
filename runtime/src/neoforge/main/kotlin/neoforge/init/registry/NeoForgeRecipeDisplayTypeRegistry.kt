package com.algorithmlx.ecr.neoforge.init.registry

import com.algorithmlx.ecr.api.ModId
import com.algorithmlx.ecr.common.init.ECRModIDs
import com.algorithmlx.ecr.registry.RecipeDisplayTypeRegistry
import com.algorithmlx.ecr.common.recipe.MithrilineFurnaceRecipe
import com.algorithmlx.ecr.common.recipe.StructureRecipe
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.crafting.display.RecipeDisplay
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister

class NeoForgeRecipeDisplayTypeRegistry(bus: IEventBus): RecipeDisplayTypeRegistry {
    private val registry = DeferredRegister.create(BuiltInRegistries.RECIPE_DISPLAY, ModId)

    init {
        registry.register(bus)
    }

    private val mithrilineFurnaceTypeRegistry = registry.register(ECRModIDs.MITHRILINE_FURNACE) { _ ->
        RecipeDisplay.Type(MithrilineFurnaceRecipe.Display.MAP_CODEC, MithrilineFurnaceRecipe.Display.STREAM_CODEC)
    }

    private val structureDisplayTypeRegistry = registry.register(ECRModIDs.STRUCTURE) { _ ->
        RecipeDisplay.Type(StructureRecipe.Display.MAP_CODEC, StructureRecipe.Display.STREAM_CODEC)
    }

    override val mithrilineFurnace: RecipeDisplay.Type<MithrilineFurnaceRecipe.Display> by lazy { mithrilineFurnaceTypeRegistry.get() }
    override val structure: RecipeDisplay.Type<StructureRecipe.Display> by lazy { structureDisplayTypeRegistry.get() }
}
