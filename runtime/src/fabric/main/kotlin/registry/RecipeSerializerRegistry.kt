package com.algorithmlx.ecr.registry

import com.algorithmlx.ecr.api.utils.ecRL
import com.algorithmlx.ecr.common.init.ECRModIDs
import com.algorithmlx.ecr.common.recipe.MagicTableRecipe
import com.algorithmlx.ecr.common.recipe.StructureRecipe
import com.algorithmlx.ecr.common.recipe.MithrilineFurnaceRecipe
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.crafting.RecipeSerializer

@Suppress("ACTUAL_WITHOUT_EXPECT", "unused")
actual object RecipeSerializerRegistry {
    actual val mithrilineFurnace: RecipeSerializer<MithrilineFurnaceRecipe> = register(
        ECRModIDs.MITHRILINE_FURNACE, RecipeSerializer(MithrilineFurnaceRecipe.CODEC, MithrilineFurnaceRecipe.STREAM_CODEC)
    )
    actual val structure: RecipeSerializer<StructureRecipe> = register(
        ECRModIDs.STRUCTURE, RecipeSerializer(StructureRecipe.CODEC, StructureRecipe.STREAM_CODEC)
    )
    actual val magicTable: RecipeSerializer<MagicTableRecipe> = register(
        ECRModIDs.MAGIC_TABLE, RecipeSerializer(MagicTableRecipe.CODEC, MagicTableRecipe.STREAM_CODEC)
    )

    private fun <T: RecipeSerializer<*>> register(id: String, recipeSerializer: T): T = Registry.register(
        BuiltInRegistries.RECIPE_SERIALIZER, id.ecRL, recipeSerializer
    )
}