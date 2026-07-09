package com.algorithmlx.ecr.fabric.init.registry

import com.algorithmlx.ecr.api.ecRL
import com.algorithmlx.ecr.common.init.ECRModIDs
import com.algorithmlx.ecr.common.init.registry.RecipeSerializerRegistry
import com.algorithmlx.ecr.common.recipe.EnvoyerRecipe
import com.algorithmlx.ecr.common.recipe.StructureRecipe
import com.algorithmlx.ecr.common.recipe.MithrilineFurnaceRecipe
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.crafting.RecipeSerializer

object FabricRecipeSerializerRegistry: RecipeSerializerRegistry {
    override val mithrilineFurnace: RecipeSerializer<MithrilineFurnaceRecipe> = register(
        ECRModIDs.MITHRILINE_FURNACE, RecipeSerializer(MithrilineFurnaceRecipe.CODEC, MithrilineFurnaceRecipe.STREAM_CODEC)
    )
    override val structure: RecipeSerializer<StructureRecipe> = register(
        ECRModIDs.STRUCTURE, RecipeSerializer(StructureRecipe.CODEC, StructureRecipe.STREAM_CODEC)
    )
    override val envoyer: RecipeSerializer<EnvoyerRecipe> = register(
        ECRModIDs.ENVOYER, RecipeSerializer(EnvoyerRecipe.CODEC, EnvoyerRecipe.STREAM_CODEC)
    )

    private fun <T: RecipeSerializer<*>> register(id: String, recipeSerializer: T): T = Registry.register(
        BuiltInRegistries.RECIPE_SERIALIZER, id.ecRL, recipeSerializer
    )
}
