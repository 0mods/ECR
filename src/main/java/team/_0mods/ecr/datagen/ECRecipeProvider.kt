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

        MithrilineFurnaceRecipeBuilder.make(Items.PUMPKIN).requires(Items.MELON).espe(1).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.MELON).requires(Items.PUMPKIN).espe(1).save(c)

        MithrilineFurnaceRecipeBuilder.make(Items.DANDELION).requires(Items.CORNFLOWER).espe(1).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.POPPY).requires(Items.DANDELION).espe(1).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.BLUE_ORCHID).requires(Items.POPPY).espe(1).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.ALLIUM).requires(Items.BLUE_ORCHID).espe(1).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.AZURE_BLUET).requires(Items.ALLIUM).espe(1).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.RED_TULIP).requires(Items.AZURE_BLUET).espe(1).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.ORANGE_TULIP).requires(Items.RED_TULIP).espe(1).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.WHITE_TULIP).requires(Items.ORANGE_TULIP).espe(1).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.PINK_TULIP).requires(Items.WHITE_TULIP).espe(1).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.OXEYE_DAISY).requires(Items.PINK_TULIP).espe(1).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.CORNFLOWER).requires(Items.OXEYE_DAISY).espe(1).save(c)

        MithrilineFurnaceRecipeBuilder.make(Items.RED_MUSHROOM).requires(Items.BROWN_MUSHROOM).espe(1).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.BROWN_MUSHROOM).requires(Items.RED_MUSHROOM).espe(1).save(c)

        MithrilineFurnaceRecipeBuilder.make(Items.ENDER_PEARL, 3).requires(Items.BLAZE_ROD, 2).espe(128).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.BLAZE_ROD, 2).requires(Items.ENDER_PEARL, 3).espe(128).save(c)

        MithrilineFurnaceRecipeBuilder.make(Items.REDSTONE, 64).requires(Items.GHAST_TEAR).espe(1024).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.GHAST_TEAR).requires(Items.REDSTONE, 64).espe(1024).save(c)

        MithrilineFurnaceRecipeBuilder.make(Items.CLAY, 12).requires(Items.GUNPOWDER).espe(32).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.GUNPOWDER).requires(Items.CLAY, 12).espe(32).save(c)

        MithrilineFurnaceRecipeBuilder.make(Items.MUSIC_DISC_13).requires(Items.MUSIC_DISC_PIGSTEP).espe(256).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.MUSIC_DISC_CAT).requires(Items.MUSIC_DISC_13).espe(256).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.MUSIC_DISC_BLOCKS).requires(Items.MUSIC_DISC_CAT).espe(256).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.MUSIC_DISC_CHIRP).requires(Items.MUSIC_DISC_BLOCKS).espe(256).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.MUSIC_DISC_FAR).requires(Items.MUSIC_DISC_CHIRP).espe(256).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.MUSIC_DISC_MALL).requires(Items.MUSIC_DISC_FAR).espe(256).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.MUSIC_DISC_MELLOHI).requires(Items.MUSIC_DISC_MALL).espe(256).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.MUSIC_DISC_STAL).requires(Items.MUSIC_DISC_MELLOHI).espe(256).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.MUSIC_DISC_STRAD).requires(Items.MUSIC_DISC_STAL).espe(256).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.MUSIC_DISC_WARD).requires(Items.MUSIC_DISC_STRAD).espe(256).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.MUSIC_DISC_11).requires(Items.MUSIC_DISC_WARD).espe(256).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.MUSIC_DISC_WAIT).requires(Items.MUSIC_DISC_11).espe(256).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.MUSIC_DISC_OTHERSIDE).requires(Items.MUSIC_DISC_WAIT).espe(256).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.MUSIC_DISC_5).requires(Items.MUSIC_DISC_OTHERSIDE).espe(256).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.MUSIC_DISC_PIGSTEP).requires(Items.MUSIC_DISC_5).espe(256).save(c)

        MithrilineFurnaceRecipeBuilder.make(Items.SUGAR, 3).requires(Items.SLIME_BALL, 4).espe(16).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.SLIME_BALL, 4).requires(Items.SUGAR, 3).espe(16).save(c)

        MithrilineFurnaceRecipeBuilder.make(Items.EGG, 9).requires(Items.BONE, 2).espe(48).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.BONE, 2).requires(Items.EGG, 9).espe(48).save(c)

        MithrilineFurnaceRecipeBuilder.make(Items.WITHER_SKELETON_SKULL).requires(Items.SKELETON_SKULL, 3).espe(1024).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.ZOMBIE_HEAD, 3).requires(Items.WITHER_SKELETON_SKULL).espe(1024).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.PLAYER_HEAD).requires(Items.ZOMBIE_HEAD).espe(1024).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.CREEPER_HEAD).requires(Items.PLAYER_HEAD).espe(1024).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.SKELETON_SKULL).requires(Items.CREEPER_HEAD).espe(1024).save(c)

        MithrilineFurnaceRecipeBuilder.make(Items.WHEAT, 3).requires(Items.LEATHER).espe(128).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.LEATHER).requires(Items.WHEAT, 3).espe(128).save(c)


        MithrilineFurnaceRecipeBuilder.make(Items.COD).requires(Items.TROPICAL_FISH).espe(24).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.TROPICAL_FISH).requires(Items.SALMON).espe(24).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.SALMON).requires(Items.PUFFERFISH).espe(24).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.PUFFERFISH).requires(Items.TROPICAL_FISH).espe(24).save(c)

        MithrilineFurnaceRecipeBuilder.make(Items.SUGAR_CANE, 3).requires(Items.FEATHER, 2).espe(64).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.FEATHER, 3).requires(Items.SUGAR_CANE, 2).espe(64).save(c)

        MithrilineFurnaceRecipeBuilder.make(Items.COAL).requires(Items.CHARCOAL).espe(1).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.CHARCOAL).requires(Items.COAL).espe(1).save(c)

        MithrilineFurnaceRecipeBuilder.make(Items.GRASS_BLOCK).requires(Items.PACKED_ICE).espe(1).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.DIRT).requires(Items.GRASS_BLOCK).espe(1).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.COARSE_DIRT).requires(Items.DIRT).espe(1).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.PODZOL).requires(Items.COARSE_DIRT).espe(1).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.GLASS).requires(Items.PODZOL).espe(1).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.TINTED_GLASS).requires(Items.GLASS).espe(1).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.ICE).requires(Items.TINTED_GLASS).espe(1).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.PACKED_ICE).requires(Items.ICE).espe(1).save(c)
        MithrilineFurnaceRecipeBuilder.make(Items.BLUE_ICE).requires(Items.PACKED_ICE).espe(1).save(c)
    }
}
