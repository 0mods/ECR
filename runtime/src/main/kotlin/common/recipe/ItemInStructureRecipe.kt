package com.algorithmlx.ecr.common.recipe

import com.algorithmlx.ecr.api.ModId
import com.algorithmlx.ecr.api.multiblock.Multiblock
import com.algorithmlx.ecr.api.registries.ECRegistries
import com.algorithmlx.ecr.common.init.registry.RecipeSerializerRegistry
import com.algorithmlx.ecr.common.init.registry.RecipeTypeRegistry
import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import io.netty.buffer.ByteBuf
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.Identifier
import net.minecraft.world.item.ItemStackTemplate
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeBookCategories
import net.minecraft.world.item.crafting.RecipeBookCategory
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.item.crafting.SingleItemRecipe
import net.minecraft.world.level.block.Block
import java.util.Optional
import kotlin.jvm.optionals.getOrNull

class ItemInStructureRecipe(
    val multiblock: Multiblock,
    val time: Int,
    ingredient: Ingredient,
    result: ItemStackTemplate,
    val chance: Range,
    val blockForPlace: Block?
): SingleItemRecipe(Recipe.CommonInfo(true), ingredient, result) {
    override fun getSerializer(): RecipeSerializer<out SingleItemRecipe> = RecipeSerializerRegistry.instance.itemInStructure

    override fun getType(): RecipeType<out SingleItemRecipe> = RecipeTypeRegistry.instance.itemInStructure

    override fun group(): String = "$ModId:item_in_structure"

    override fun recipeBookCategory(): RecipeBookCategory = RecipeBookCategories.CAMPFIRE

    companion object {
        @JvmField
        val CODEC: MapCodec<ItemInStructureRecipe> = RecordCodecBuilder.mapCodec {
            it.group(
                Identifier.CODEC.fieldOf("multiblock")
                    .forGetter { fg -> ECRegistries.MULTIBLOCK.getKey(fg.multiblock) ?: throw NullPointerException("Multiblock is not registered") },
                Codec.INT.fieldOf("time").forGetter(ItemInStructureRecipe::time),
                Ingredient.CODEC.fieldOf("ingredient").forGetter(ItemInStructureRecipe::input),
                ItemStackTemplate.CODEC.fieldOf("result").forGetter(ItemInStructureRecipe::result),
                Range.CODEC.fieldOf("chance").forGetter(ItemInStructureRecipe::chance),
                Codec.optionalField("placement", Identifier.CODEC, true)
                    .forGetter { fg -> Optional.ofNullable(fg.blockForPlace?.let { thing -> BuiltInRegistries.BLOCK.getKey(thing) }) }
            ).apply(it) { multiblockId, time, ingredient, result, chance, placement ->
                ItemInStructureRecipe(
                    ECRegistries.MULTIBLOCK.getOptional(multiblockId).getOrNull() ?: throw NullPointerException("Multiblock is not registered"),
                    time, ingredient, result,
                    chance, placement.getOrNull()?.let { l ->
                        BuiltInRegistries.BLOCK.getOptional(l).getOrNull()
                    }
                )
            }
        }

        @JvmField
        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, ItemInStructureRecipe> = StreamCodec.of(
            ::toNetwork, ::fromNetwork
        )

        private fun fromNetwork(buf: RegistryFriendlyByteBuf): ItemInStructureRecipe {
            val multiblockId = Identifier.STREAM_CODEC.decode(buf)
            val time = buf.readInt()
            val ingredient = Ingredient.CONTENTS_STREAM_CODEC.decode(buf)
            val result = ItemStackTemplate.STREAM_CODEC.decode(buf)
            val chance = Range.STREAM_CODEC.decode(buf)
            val blockForPlaceId = buf.readNullable { Identifier.STREAM_CODEC.decode(it) }

            val multiblock = ECRegistries.MULTIBLOCK.getOptional(multiblockId).getOrNull() ?: throw NullPointerException("Multiblock is not registered")
            val blockForPlace = BuiltInRegistries.BLOCK.getOptional(blockForPlaceId).getOrNull()
            return ItemInStructureRecipe(multiblock, time, ingredient, result, chance, blockForPlace)
        }

        private fun toNetwork(buf: RegistryFriendlyByteBuf, recipe: ItemInStructureRecipe) {
            Identifier.STREAM_CODEC.encode(buf, ECRegistries.MULTIBLOCK.getKey(recipe.multiblock) ?: throw NullPointerException("Multiblock is not registered"))
            buf.writeInt(recipe.time)
            Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.input())
            ItemStackTemplate.STREAM_CODEC.encode(buf, recipe.result())
            Range.STREAM_CODEC.encode(buf, recipe.chance)
            buf.writeNullable(recipe.blockForPlace?.let { BuiltInRegistries.BLOCK.getKey(it) }, Identifier.STREAM_CODEC)
        }
    }

    data class Range(val min: Int, val max: Int){
        companion object {
            @JvmField
            val CODEC: Codec<Range> = RecordCodecBuilder.create {
                it.group(
                    Codec.INT.fieldOf("min").forGetter(Range::min),
                    Codec.INT.fieldOf("max").forGetter(Range::max)
                ).apply(it, ::Range)
            }

            @JvmField
            val STREAM_CODEC: StreamCodec<ByteBuf, Range> = StreamCodec.composite(
                ByteBufCodecs.INT, Range::min,
                ByteBufCodecs.INT, Range::max,
                ::Range
            )
        }
    }
}
