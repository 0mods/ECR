package com.algorithmlx.ecr.registry

import com.algorithmlx.ecr.api.utils.ecRL
import com.algorithmlx.ecr.common.init.ECRModIDs
import com.algorithmlx.ecr.common.recipe.MithrilineFurnaceRecipe
import com.algorithmlx.ecr.common.recipe.StructureRecipe
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.crafting.display.RecipeDisplay

@Suppress("ACTUAL_WITHOUT_EXPECT", "unused")
actual object RecipeDisplayTypeRegistry {
    actual val mithrilineFurnace: RecipeDisplay.Type<MithrilineFurnaceRecipe.Display> = register(
        ECRModIDs.MITHRILINE_FURNACE,
        RecipeDisplay.Type(MithrilineFurnaceRecipe.Display.MAP_CODEC, MithrilineFurnaceRecipe.Display.STREAM_CODEC)
    )
    actual val structure: RecipeDisplay.Type<StructureRecipe.Display> = register(
        ECRModIDs.STRUCTURE,
        RecipeDisplay.Type(StructureRecipe.Display.MAP_CODEC, StructureRecipe.Display.STREAM_CODEC)
    )

    private fun <T: RecipeDisplay.Type<*>> register(id: String, type: T): T = Registry.register(
        BuiltInRegistries.RECIPE_DISPLAY, id.ecRL, type
    )
}