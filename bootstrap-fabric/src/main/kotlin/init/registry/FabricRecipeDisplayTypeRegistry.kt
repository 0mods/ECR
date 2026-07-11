package com.algorithmlx.ecr.fabric.init.registry

import com.algorithmlx.ecr.api.ecRL
import com.algorithmlx.ecr.common.init.ECRModIDs
import com.algorithmlx.ecr.common.init.registry.RecipeDisplayTypeRegistry
import com.algorithmlx.ecr.common.recipe.MithrilineFurnaceRecipe
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.crafting.display.RecipeDisplay

object FabricRecipeDisplayTypeRegistry: RecipeDisplayTypeRegistry {
    override val mithrilineFurnace: RecipeDisplay.Type<MithrilineFurnaceRecipe.Display> = register(
        ECRModIDs.MITHRILINE_FURNACE,
        RecipeDisplay.Type(MithrilineFurnaceRecipe.Display.MAP_CODEC, MithrilineFurnaceRecipe.Display.STREAM_CODEC)
    )

    private fun <T: RecipeDisplay.Type<*>> register(id: String, type: T): T = Registry.register(
        BuiltInRegistries.RECIPE_DISPLAY, id.ecRL, type
    )
}
