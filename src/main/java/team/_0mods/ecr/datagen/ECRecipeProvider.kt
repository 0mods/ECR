package team._0mods.ecr.datagen

import net.minecraft.data.DataGenerator
import net.minecraft.data.recipes.FinishedRecipe
import net.minecraft.data.recipes.RecipeProvider
import net.minecraft.world.item.Items
import team._0mods.ecr.datagen.builder.MithrilineFurnaceRecipeBuilder
import java.util.function.Consumer

class ECRecipeProvider(generator: DataGenerator) : RecipeProvider(generator) {
    override fun buildCraftingRecipes(c: Consumer<FinishedRecipe>) {
        MithrilineFurnaceRecipeBuilder.make(Items.GOLD_INGOT).requires(Items.IRON_INGOT, 8).espe(64).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.IRON_INGOT, 8).requires(Items.GOLD_INGOT).espe(64).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.EMERALD).requires(Items.DIAMOND, 2).espe(512).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.DIAMOND, 2).requires(Items.EMERALD).espe(512).save(c)

        MithrilineFurnaceRecipeBuilder.make(Items.OAK_PLANKS).requires(Items.CRIMSON_PLANKS).espe(1).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.BIRCH_PLANKS).requires(Items.OAK_PLANKS).espe(1).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.ACACIA_PLANKS).requires(Items.BIRCH_PLANKS).espe(1).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.JUNGLE_PLANKS).requires(Items.ACACIA_PLANKS).espe(1).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.SPRUCE_PLANKS).requires(Items.JUNGLE_PLANKS).espe(1).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.DARK_OAK_PLANKS).requires(Items.SPRUCE_PLANKS).espe(1).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.MANGROVE_PLANKS).requires(Items.DARK_OAK_PLANKS).espe(1).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.WARPED_PLANKS).requires(Items.MANGROVE_PLANKS).espe(1).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.CRIMSON_PLANKS).requires(Items.WARPED_PLANKS).espe(1).save(c)

        MithrilineFurnaceRecipeBuilder.make(Items.OAK_SAPLING).requires(Items.MANGROVE_PROPAGULE).espe(1).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.BIRCH_SAPLING).requires(Items.OAK_SAPLING).espe(1).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.ACACIA_SAPLING).requires(Items.BIRCH_SAPLING).espe(1).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.JUNGLE_SAPLING).requires(Items.ACACIA_SAPLING).espe(1).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.SPRUCE_SAPLING).requires(Items.JUNGLE_SAPLING).espe(1).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.DARK_OAK_SAPLING).requires(Items.SPRUCE_SAPLING).espe(1).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.MANGROVE_PROPAGULE).requires(Items.DARK_OAK_SAPLING).espe(1).save(c)

        MithrilineFurnaceRecipeBuilder.make(Items.OAK_LOG).requires(Items.CRIMSON_STEM).espe(1).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.BIRCH_LOG).requires(Items.OAK_LOG).espe(1).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.ACACIA_LOG).requires(Items.BIRCH_LOG).espe(1).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.JUNGLE_LOG).requires(Items.ACACIA_LOG).espe(1).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.SPRUCE_LOG).requires(Items.JUNGLE_LOG).espe(1).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.DARK_OAK_LOG).requires(Items.SPRUCE_LOG).espe(1).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.MANGROVE_LOG).requires(Items.DARK_OAK_LOG).espe(1).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.WARPED_STEM).requires(Items.MANGROVE_LOG).espe(1).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.CRIMSON_STEM).requires(Items.WARPED_STEM).espe(1).save(c)

        MithrilineFurnaceRecipeBuilder.make(Items.OAK_WOOD).requires(Items.CRIMSON_HYPHAE).espe(1).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.BIRCH_WOOD).requires(Items.OAK_WOOD).espe(1).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.ACACIA_WOOD).requires(Items.BIRCH_WOOD).espe(1).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.JUNGLE_WOOD).requires(Items.ACACIA_WOOD).espe(1).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.SPRUCE_WOOD).requires(Items.JUNGLE_WOOD).espe(1).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.DARK_OAK_WOOD).requires(Items.SPRUCE_WOOD).espe(1).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.MANGROVE_WOOD).requires(Items.DARK_OAK_WOOD).espe(1).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.WARPED_HYPHAE).requires(Items.MANGROVE_WOOD).espe(1).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.CRIMSON_HYPHAE).requires(Items.WARPED_HYPHAE).espe(1).save(c)

        MithrilineFurnaceRecipeBuilder.make(Items.STRIPPED_OAK_WOOD).requires(Items.STRIPPED_CRIMSON_HYPHAE).espe(1).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.STRIPPED_BIRCH_WOOD).requires(Items.STRIPPED_OAK_WOOD).espe(1).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.STRIPPED_ACACIA_WOOD).requires(Items.STRIPPED_BIRCH_WOOD).espe(1).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.STRIPPED_JUNGLE_WOOD).requires(Items.STRIPPED_ACACIA_WOOD).espe(1).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.STRIPPED_SPRUCE_WOOD).requires(Items.STRIPPED_JUNGLE_WOOD).espe(1).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.STRIPPED_DARK_OAK_WOOD).requires(Items.STRIPPED_SPRUCE_WOOD).espe(1).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.STRIPPED_MANGROVE_WOOD).requires(Items.STRIPPED_DARK_OAK_WOOD).espe(1).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.STRIPPED_WARPED_HYPHAE).requires(Items.STRIPPED_MANGROVE_WOOD).espe(1).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.STRIPPED_CRIMSON_HYPHAE).requires(Items.STRIPPED_WARPED_HYPHAE).espe(1).save(c)

        MithrilineFurnaceRecipeBuilder.make(Items.STRIPPED_OAK_LOG).requires(Items.STRIPPED_CRIMSON_STEM).espe(1).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.STRIPPED_BIRCH_LOG).requires(Items.STRIPPED_OAK_LOG).espe(1).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.STRIPPED_ACACIA_LOG).requires(Items.STRIPPED_BIRCH_LOG).espe(1).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.STRIPPED_JUNGLE_LOG).requires(Items.STRIPPED_ACACIA_LOG).espe(1).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.STRIPPED_SPRUCE_LOG).requires(Items.STRIPPED_JUNGLE_LOG).espe(1).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.STRIPPED_DARK_OAK_LOG).requires(Items.STRIPPED_SPRUCE_LOG).espe(1).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.STRIPPED_MANGROVE_LOG).requires(Items.STRIPPED_DARK_OAK_LOG).espe(1).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.STRIPPED_WARPED_STEM).requires(Items.STRIPPED_MANGROVE_LOG).espe(1).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.STRIPPED_CRIMSON_STEM).requires(Items.STRIPPED_WARPED_STEM).espe(1).save(c)

        MithrilineFurnaceRecipeBuilder.make(Items.OAK_LEAVES).requires(Items.MANGROVE_LEAVES).espe(1).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.BIRCH_LEAVES).requires(Items.OAK_LEAVES).espe(1).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.ACACIA_LEAVES).requires(Items.BIRCH_LEAVES).espe(1).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.JUNGLE_LEAVES).requires(Items.ACACIA_LEAVES).espe(1).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.SPRUCE_LEAVES).requires(Items.JUNGLE_LEAVES).espe(1).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.DARK_OAK_LEAVES).requires(Items.SPRUCE_LEAVES).espe(1).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.MANGROVE_LEAVES).requires(Items.DARK_OAK_LEAVES).espe(1).save(c)
    }
}