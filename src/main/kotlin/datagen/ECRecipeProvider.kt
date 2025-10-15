package team._0mods.ecr.datagen

import net.minecraft.data.PackOutput
import net.minecraft.data.recipes.FinishedRecipe
import net.minecraft.data.recipes.RecipeProvider
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.ItemLike
import team._0mods.ecr.common.helper.ofStack
import team._0mods.ecr.datagen.builder.MithrilineFurnaceRecipeBuilder
import java.util.function.Consumer

class ECRecipeProvider(output: PackOutput) : RecipeProvider(output) {
    fun make(itemIn: ItemStack, itemOut: ItemStack, espe: Int, c: Consumer<FinishedRecipe>) =
        MithrilineFurnaceRecipeBuilder.make(itemIn.item, itemIn.count)
            .espe(espe)
            .requires(itemOut.item, itemOut.count)
            .save(c)

    fun make(itemIn: ItemLike, itemOut: ItemStack, espe: Int, c: Consumer<FinishedRecipe>) =
        make(ItemStack(itemIn), itemOut, espe, c)

    fun make(itemIn: ItemStack, itemOut: ItemLike, espe: Int, c: Consumer<FinishedRecipe>) =
        make(itemIn, ItemStack(itemOut), espe, c)

    fun make(itemIn: ItemLike, itemOut: ItemLike, espe: Int, c: Consumer<FinishedRecipe>) =
        make(ItemStack(itemIn), ItemStack(itemOut), espe, c)
    
    override fun buildRecipes(c: Consumer<FinishedRecipe>) {
        make(Items.GOLD_INGOT, Items.IRON_INGOT.ofStack(8), 64, c)
        make(Items.IRON_INGOT.ofStack(8), Items.GOLD_INGOT, 64, c)

        make(Items.EMERALD, Items.DIAMOND.ofStack(2), 512, c)
        make(Items.DIAMOND.ofStack(2), Items.EMERALD, 512, c)

        make(Items.OAK_PLANKS, Items.CRIMSON_PLANKS, 1, c)
        make(Items.BIRCH_PLANKS, Items.OAK_PLANKS, 1, c)
        make(Items.ACACIA_PLANKS, Items.BIRCH_PLANKS, 1, c)
        make(Items.JUNGLE_PLANKS, Items.ACACIA_PLANKS, 1, c)
        make(Items.SPRUCE_PLANKS, Items.JUNGLE_PLANKS, 1, c)
        make(Items.DARK_OAK_PLANKS, Items.SPRUCE_PLANKS, 1, c)
        make(Items.MANGROVE_PLANKS, Items.DARK_OAK_PLANKS, 1, c)
        make(Items.WARPED_PLANKS, Items.MANGROVE_PLANKS, 1, c)
        make(Items.CRIMSON_PLANKS, Items.WARPED_PLANKS, 1, c)

        make(Items.OAK_SAPLING, Items.MANGROVE_PROPAGULE, 1, c)
        make(Items.BIRCH_SAPLING, Items.OAK_SAPLING, 1, c)
        make(Items.ACACIA_SAPLING, Items.BIRCH_SAPLING, 1, c)
        make(Items.JUNGLE_SAPLING, Items.ACACIA_SAPLING, 1, c)
        make(Items.SPRUCE_SAPLING, Items.JUNGLE_SAPLING, 1, c)
        make(Items.DARK_OAK_SAPLING, Items.SPRUCE_SAPLING, 1, c)
        make(Items.MANGROVE_PROPAGULE, Items.DARK_OAK_SAPLING, 1, c)

        make(Items.OAK_LOG, Items.CRIMSON_STEM, 1, c)
        make(Items.BIRCH_LOG, Items.OAK_LOG, 1, c)
        make(Items.ACACIA_LOG, Items.BIRCH_LOG, 1, c)
        make(Items.JUNGLE_LOG, Items.ACACIA_LOG, 1, c)
        make(Items.SPRUCE_LOG, Items.JUNGLE_LOG, 1, c)
        make(Items.DARK_OAK_LOG, Items.SPRUCE_LOG, 1, c)
        make(Items.MANGROVE_LOG, Items.DARK_OAK_LOG, 1, c)
        make(Items.WARPED_STEM, Items.MANGROVE_LOG, 1, c)
        make(Items.CRIMSON_STEM, Items.WARPED_STEM, 1, c)

        make(Items.OAK_WOOD, Items.CRIMSON_HYPHAE, 1, c)
        make(Items.BIRCH_WOOD, Items.OAK_WOOD, 1, c)
        make(Items.ACACIA_WOOD, Items.BIRCH_WOOD, 1, c)
        make(Items.JUNGLE_WOOD, Items.ACACIA_WOOD, 1, c)
        make(Items.SPRUCE_WOOD, Items.JUNGLE_WOOD, 1, c)
        make(Items.DARK_OAK_WOOD, Items.SPRUCE_WOOD, 1, c)
        make(Items.MANGROVE_WOOD, Items.DARK_OAK_WOOD, 1, c)
        make(Items.WARPED_HYPHAE, Items.MANGROVE_WOOD, 1, c)
        make(Items.CRIMSON_HYPHAE, Items.WARPED_HYPHAE, 1, c)

        make(Items.STRIPPED_OAK_WOOD, Items.STRIPPED_CRIMSON_HYPHAE, 1, c)
        make(Items.STRIPPED_BIRCH_WOOD, Items.STRIPPED_OAK_WOOD, 1, c)
        make(Items.STRIPPED_ACACIA_WOOD, Items.STRIPPED_BIRCH_WOOD, 1, c)
        make(Items.STRIPPED_JUNGLE_WOOD, Items.STRIPPED_ACACIA_WOOD, 1, c)
        make(Items.STRIPPED_SPRUCE_WOOD, Items.STRIPPED_JUNGLE_WOOD, 1, c)
        make(Items.STRIPPED_DARK_OAK_WOOD, Items.STRIPPED_SPRUCE_WOOD, 1, c)
        make(Items.STRIPPED_MANGROVE_WOOD, Items.STRIPPED_DARK_OAK_WOOD, 1, c)
        make(Items.STRIPPED_WARPED_HYPHAE, Items.STRIPPED_MANGROVE_WOOD, 1, c)
        make(Items.STRIPPED_CRIMSON_HYPHAE, Items.STRIPPED_WARPED_HYPHAE, 1, c)

        make(Items.STRIPPED_OAK_LOG, Items.STRIPPED_CRIMSON_STEM, 1, c)
        make(Items.STRIPPED_BIRCH_LOG, Items.STRIPPED_OAK_LOG, 1, c)
        make(Items.STRIPPED_ACACIA_LOG, Items.STRIPPED_BIRCH_LOG, 1, c)
        make(Items.STRIPPED_JUNGLE_LOG, Items.STRIPPED_ACACIA_LOG, 1, c)
        make(Items.STRIPPED_SPRUCE_LOG, Items.STRIPPED_JUNGLE_LOG, 1, c)
        make(Items.STRIPPED_DARK_OAK_LOG, Items.STRIPPED_SPRUCE_LOG, 1, c)
        make(Items.STRIPPED_MANGROVE_LOG, Items.STRIPPED_DARK_OAK_LOG, 1, c)
        make(Items.STRIPPED_WARPED_STEM, Items.STRIPPED_MANGROVE_LOG, 1, c)
        make(Items.STRIPPED_CRIMSON_STEM, Items.STRIPPED_WARPED_STEM, 1, c)

        make(Items.OAK_LEAVES, Items.MANGROVE_LEAVES, 1, c)
        make(Items.BIRCH_LEAVES, Items.OAK_LEAVES, 1, c)
        make(Items.ACACIA_LEAVES, Items.BIRCH_LEAVES, 1, c)
        make(Items.JUNGLE_LEAVES, Items.ACACIA_LEAVES, 1, c)
        make(Items.SPRUCE_LEAVES, Items.JUNGLE_LEAVES, 1, c)
        make(Items.DARK_OAK_LEAVES, Items.SPRUCE_LEAVES, 1, c)
        make(Items.MANGROVE_LEAVES, Items.DARK_OAK_LEAVES, 1, c)

        make(Items.PUMPKIN, Items.MELON, 1, c)
        make(Items.MELON, Items.PUMPKIN, 1, c)

        make(Items.DANDELION, Items.CORNFLOWER, 1, c)
        make(Items.POPPY, Items.DANDELION, 1, c)
        make(Items.BLUE_ORCHID, Items.POPPY, 1, c)
        make(Items.ALLIUM, Items.BLUE_ORCHID, 1, c)
        make(Items.AZURE_BLUET, Items.ALLIUM, 1, c)
        make(Items.RED_TULIP, Items.AZURE_BLUET, 1, c)
        make(Items.ORANGE_TULIP, Items.RED_TULIP, 1, c)
        make(Items.WHITE_TULIP, Items.ORANGE_TULIP, 1, c)
        make(Items.PINK_TULIP, Items.WHITE_TULIP, 1, c)
        make(Items.OXEYE_DAISY, Items.PINK_TULIP, 1, c)
        make(Items.CORNFLOWER, Items.OXEYE_DAISY, 1, c)

        make(Items.RED_MUSHROOM, Items.BROWN_MUSHROOM, 1, c)
        make(Items.BROWN_MUSHROOM, Items.RED_MUSHROOM, 1, c)

        make(Items.ENDER_PEARL.ofStack(3), Items.BLAZE_ROD.ofStack(2), 128, c)
        make(Items.BLAZE_ROD.ofStack(2), Items.ENDER_PEARL.ofStack(3), 128, c)

        make(Items.REDSTONE.ofStack(64), Items.GHAST_TEAR, 1024, c)
        make(Items.GHAST_TEAR, Items.REDSTONE.ofStack(64), 1024, c)

        make(Items.CLAY.ofStack(12), Items.GUNPOWDER, 32, c)
        make(Items.GUNPOWDER, Items.CLAY.ofStack(12), 32, c)

        make(Items.MUSIC_DISC_13, Items.MUSIC_DISC_PIGSTEP, 256, c)
        make(Items.MUSIC_DISC_CAT, Items.MUSIC_DISC_13, 256, c)
        make(Items.MUSIC_DISC_BLOCKS, Items.MUSIC_DISC_CAT, 256, c)
        make(Items.MUSIC_DISC_CHIRP, Items.MUSIC_DISC_BLOCKS, 256, c)
        make(Items.MUSIC_DISC_FAR, Items.MUSIC_DISC_CHIRP, 256, c)
        make(Items.MUSIC_DISC_MALL, Items.MUSIC_DISC_FAR, 256, c)
        make(Items.MUSIC_DISC_MELLOHI, Items.MUSIC_DISC_MALL, 256, c)
        make(Items.MUSIC_DISC_STAL, Items.MUSIC_DISC_MELLOHI, 256, c)
        make(Items.MUSIC_DISC_STRAD, Items.MUSIC_DISC_STAL, 256, c)
        make(Items.MUSIC_DISC_WARD, Items.MUSIC_DISC_STRAD, 256, c)
        make(Items.MUSIC_DISC_11, Items.MUSIC_DISC_WARD, 256, c)
        make(Items.MUSIC_DISC_WAIT, Items.MUSIC_DISC_11, 256, c)
        make(Items.MUSIC_DISC_OTHERSIDE, Items.MUSIC_DISC_WAIT, 256, c)
        make(Items.MUSIC_DISC_5, Items.MUSIC_DISC_OTHERSIDE, 256, c)
        make(Items.MUSIC_DISC_PIGSTEP, Items.MUSIC_DISC_5, 256, c)

        make(Items.SUGAR.ofStack(3), Items.SLIME_BALL.ofStack(4), 16, c)
        make(Items.SLIME_BALL.ofStack(4), Items.SUGAR.ofStack(3), 16, c)

        make(Items.EGG.ofStack(9), Items.BONE.ofStack(2), 48, c)
        make(Items.BONE.ofStack(2), Items.EGG.ofStack(9), 48, c)

        make(Items.WITHER_SKELETON_SKULL, Items.SKELETON_SKULL.ofStack(3), 1024, c)
        make(Items.ZOMBIE_HEAD.ofStack(3), Items.WITHER_SKELETON_SKULL, 1024, c)
        make(Items.PLAYER_HEAD, Items.ZOMBIE_HEAD, 1024, c)
        make(Items.CREEPER_HEAD, Items.PLAYER_HEAD, 1024, c)
        make(Items.SKELETON_SKULL, Items.CREEPER_HEAD, 1024, c)

        make(Items.WHEAT.ofStack(3), Items.LEATHER, 128, c)
        make(Items.LEATHER, Items.WHEAT.ofStack(3), 128, c)


        make(Items.COD, Items.TROPICAL_FISH, 24, c)
        make(Items.TROPICAL_FISH, Items.SALMON, 24, c)
        make(Items.SALMON, Items.PUFFERFISH, 24, c)
        make(Items.PUFFERFISH, Items.TROPICAL_FISH, 24, c)

        make(Items.SUGAR_CANE.ofStack(3), Items.FEATHER.ofStack(2), 64, c)
        make(Items.FEATHER.ofStack(3), Items.SUGAR_CANE.ofStack(2), 64, c)

        make(Items.COAL, Items.CHARCOAL, 1, c)
        make(Items.CHARCOAL, Items.COAL, 1, c)

        make(Items.GRASS_BLOCK, Items.PACKED_ICE, 1, c)
        make(Items.DIRT, Items.GRASS_BLOCK, 1, c)
        make(Items.COARSE_DIRT, Items.DIRT, 1, c)
        make(Items.PODZOL, Items.COARSE_DIRT, 1, c)
        make(Items.GLASS, Items.PODZOL, 1, c)
        make(Items.TINTED_GLASS, Items.GLASS, 1, c)
        make(Items.ICE, Items.TINTED_GLASS, 1, c)
        make(Items.PACKED_ICE, Items.ICE, 1, c)
        make(Items.BLUE_ICE, Items.PACKED_ICE, 1, c)
    }
}
