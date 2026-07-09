package com.algorithmlx.ecr.common.recipe

import com.algorithmlx.ecr.api.ModId
import com.algorithmlx.ecr.common.init.ECRModIDs
import com.algorithmlx.ecr.common.init.registry.RecipeSerializerRegistry
import com.algorithmlx.ecr.common.init.registry.RecipeTypeRegistry
import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.ItemStackTemplate
import net.minecraft.world.item.crafting.CraftingInput
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.item.crafting.PlacementInfo
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeBookCategories
import net.minecraft.world.item.crafting.RecipeBookCategory
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.level.Level

class MithrilineFurnaceRecipe(
    val input: Ingredient,
    val ingredientCount: Int,
    val espe: Int,
    private val result: ItemStackTemplate
): Recipe<CraftingInput> {
    override fun matches(
        input: CraftingInput,
        level: Level
    ): Boolean {
        if (level.isClientSide) return false

        if (input.items().size > 1 || input.items().isEmpty()) return false

        val inputIngredient = input.items()[0]
        return this.input.test(inputIngredient)
    }

    override fun assemble(input: CraftingInput): ItemStack = this.result.create()

    override fun showNotification(): Boolean = false
    override fun group(): String = "$ModId:${ECRModIDs.MITHRILINE_FURNACE}"
    override fun getSerializer(): RecipeSerializer<out Recipe<CraftingInput>> = RecipeSerializerRegistry.instance.mithrilineFurnace
    override fun getType(): RecipeType<out Recipe<CraftingInput>> = RecipeTypeRegistry.instance.mithrilineFurnace
    override fun placementInfo(): PlacementInfo = PlacementInfo.NOT_PLACEABLE
    override fun recipeBookCategory(): RecipeBookCategory = RecipeBookCategories.FURNACE_MISC

    companion object {
        @JvmField
        val CODEC: MapCodec<MithrilineFurnaceRecipe> = RecordCodecBuilder.mapCodec {
            it.group(
                Ingredient.CODEC.fieldOf("input").forGetter(MithrilineFurnaceRecipe::input),
                Codec.intRange(1, 64).fieldOf("ingredient_count").forGetter(MithrilineFurnaceRecipe::ingredientCount),
                Codec.INT.fieldOf("espe").forGetter(MithrilineFurnaceRecipe::espe),
                ItemStackTemplate.CODEC.fieldOf("result").forGetter(MithrilineFurnaceRecipe::result)
            ).apply(it, ::MithrilineFurnaceRecipe)
        }

        @JvmField
        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, MithrilineFurnaceRecipe> = StreamCodec.of(
            ::toNetwork, ::fromNetwork
        )

        private fun fromNetwork(buf: RegistryFriendlyByteBuf): MithrilineFurnaceRecipe {
            val input = Ingredient.CONTENTS_STREAM_CODEC.decode(buf)
            val ingredientCount = buf.readInt()
            if (ingredientCount !in 1..64) throw IllegalArgumentException("ingredient_count must be from 1 to 64")
            val espe = buf.readInt()
            val result = ItemStackTemplate.STREAM_CODEC.decode(buf)
            return MithrilineFurnaceRecipe(input, ingredientCount, espe, result)
        }

        private fun toNetwork(buf: RegistryFriendlyByteBuf, recipe: MithrilineFurnaceRecipe) {
            Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.input)
            buf.writeInt(recipe.ingredientCount)
            buf.writeInt(recipe.espe)
            ItemStackTemplate.STREAM_CODEC.encode(buf, recipe.result)
        }
    }
}
