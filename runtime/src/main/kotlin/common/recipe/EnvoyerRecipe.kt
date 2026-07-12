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
import net.minecraft.world.item.crafting.ShapedRecipePattern
import net.minecraft.world.level.Level
import java.util.Optional

class EnvoyerRecipe(
    val inputs: Optional<ShapedRecipePattern>,
    val catalyst: Optional<Ingredient>,
    val time: Int,
    val mruPerTick: Int,
    val result: ItemStackTemplate
): Recipe<CraftingInput> {
    init {
        require(inputs.isPresent || catalyst.isPresent) { "Recipe must present with inputs or catalyst!" }
        require(inputs.isPresent && inputs.get().height() * inputs.get().width() < 5) { "Recipe max have only ~2x2 recipe grid" }
    }

    override fun matches(
        input: CraftingInput,
        level: Level
    ): Boolean {
        if (inputs.isPresent) {
            val shaped = inputs.get()
            val craftingInput = CraftingInput.of(2, 2, input.items().filterIndexed { index, _ -> index < 4 })

            if (!shaped.matches(craftingInput)) return false
        }

        val catalystIsEmpty = catalyst.isEmpty && input.items().getOrElse(4) { ItemStack.EMPTY }.isEmpty
        val catalystTest = input.items().size > 4 && catalyst.get().test(input.items()[4])
        return catalystIsEmpty || catalystTest
    }

    override fun assemble(input: CraftingInput): ItemStack = this.result.create()
    override fun showNotification(): Boolean = true
    override fun group(): String = "$ModId:${ECRModIDs.ENVOYER}"
    override fun getSerializer(): RecipeSerializer<out Recipe<CraftingInput>> = RecipeSerializerRegistry.instance.envoyer
    override fun getType(): RecipeType<out Recipe<CraftingInput>> = RecipeTypeRegistry.instance.envoyer
    override fun placementInfo(): PlacementInfo = PlacementInfo.NOT_PLACEABLE
    override fun recipeBookCategory(): RecipeBookCategory = RecipeBookCategories.CAMPFIRE

    companion object {
        @JvmField
        val CODEC: MapCodec<EnvoyerRecipe> = RecordCodecBuilder.mapCodec {
            it.group(
                ShapedRecipePattern.MAP_CODEC.codec()
                    .optionalFieldOf("input").forGetter(EnvoyerRecipe::inputs),
                Ingredient.CODEC.optionalFieldOf("catalyst").forGetter(EnvoyerRecipe::catalyst),
                Codec.INT.fieldOf("time").forGetter(EnvoyerRecipe::time),
                Codec.INT.fieldOf("mru").forGetter(EnvoyerRecipe::mruPerTick),
                ItemStackTemplate.MAP_CODEC.fieldOf("result").forGetter(EnvoyerRecipe::result)
            ).apply(it, ::EnvoyerRecipe)
        }

        @JvmField
        val STREAM_CODEC = StreamCodec.of(::toNetwork, ::fromNetwork)

        private fun fromNetwork(buf: RegistryFriendlyByteBuf): EnvoyerRecipe {
            val pattern = buf.readOptional { ShapedRecipePattern.STREAM_CODEC.decode(it as RegistryFriendlyByteBuf) }
            val catalyst = Ingredient.OPTIONAL_CONTENTS_STREAM_CODEC.decode(buf)
            val time = buf.readInt()
            val mru = buf.readInt()
            val result = ItemStackTemplate.STREAM_CODEC.decode(buf)
            return EnvoyerRecipe(pattern, catalyst, time, mru, result)
        }

        private fun toNetwork(buf: RegistryFriendlyByteBuf, recipe: EnvoyerRecipe) {
            buf.writeOptional(recipe.inputs) { b, pattern -> ShapedRecipePattern.STREAM_CODEC.encode(b as RegistryFriendlyByteBuf, pattern) }
            Ingredient.OPTIONAL_CONTENTS_STREAM_CODEC.encode(buf, recipe.catalyst)
            buf.writeInt(recipe.time)
            buf.writeInt(recipe.mruPerTick)
            ItemStackTemplate.STREAM_CODEC.encode(buf, recipe.result)
        }
    }
}
