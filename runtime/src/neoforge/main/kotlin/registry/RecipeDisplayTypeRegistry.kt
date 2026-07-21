package com.algorithmlx.ecr.registry

import com.algorithmlx.ecr.api.ModId
import com.algorithmlx.ecr.common.init.ECRModIDs
import com.algorithmlx.ecr.common.recipe.MithrilineFurnaceRecipe
import com.algorithmlx.ecr.common.recipe.StructureRecipe
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.crafting.display.RecipeDisplay
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister

@Suppress("ACTUAL_WITHOUT_EXPECT", "unused")
actual object RecipeDisplayTypeRegistry {
    private val registry = DeferredRegister.create(BuiltInRegistries.RECIPE_DISPLAY, ModId)

    fun init(bus: IEventBus) {
        registry.register(bus)
    }

    private val mithrilineFurnaceTypeRegistry = registry.register(ECRModIDs.MITHRILINE_FURNACE) { _ ->
        RecipeDisplay.Type(MithrilineFurnaceRecipe.Display.MAP_CODEC, MithrilineFurnaceRecipe.Display.STREAM_CODEC)
    }

    private val structureDisplayTypeRegistry = registry.register(ECRModIDs.STRUCTURE) { _ ->
        RecipeDisplay.Type(StructureRecipe.Display.MAP_CODEC, StructureRecipe.Display.STREAM_CODEC)
    }

    actual val mithrilineFurnace: RecipeDisplay.Type<MithrilineFurnaceRecipe.Display> by lazy { mithrilineFurnaceTypeRegistry.get() }
    actual val structure: RecipeDisplay.Type<StructureRecipe.Display> by lazy { structureDisplayTypeRegistry.get() }
}
