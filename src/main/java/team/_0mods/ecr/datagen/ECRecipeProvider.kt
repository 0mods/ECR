package team._0mods.ecr.datagen

import net.minecraft.data.DataGenerator
import net.minecraft.data.recipes.FinishedRecipe
import net.minecraft.data.recipes.RecipeProvider
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Items
import team._0mods.ecr.ModId
import team._0mods.ecr.api.utils.rl
import team._0mods.ecr.datagen.builder.MithrilineFurnaceRecipeBuilder
import java.util.function.Consumer

class ECRecipeProvider(generator: DataGenerator) : RecipeProvider(generator) {
    override fun buildCraftingRecipes(consumer: Consumer<FinishedRecipe>) {
        team._0mods.ecr.LOGGER.info("Recipes are generated")
        MithrilineFurnaceRecipeBuilder.make(Items.GOLD_INGOT).requires(Items.IRON_INGOT, 8).espe(64)
            .save(consumer, "gold_ingot".prefix)
    }

    private val String.prefix: ResourceLocation get() = "$ModId:$this".rl
}