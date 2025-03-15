package team._0mods.ecr.common.recipes

import com.google.gson.JsonObject
import com.google.gson.JsonSyntaxException
import net.minecraft.core.NonNullList
import net.minecraft.core.RegistryAccess
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.GsonHelper
import net.minecraft.world.SimpleContainer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.item.crafting.ShapedRecipe
import net.minecraft.world.level.Level
import team._0mods.ecr.common.init.registry.ECRegistry

open class XLikeRecipe(
    private val type: RecipeType<*>,
    private val serial: Serializer,
    private val id: ResourceLocation,
    private val inputs: NonNullList<Ingredient>,
    private val catalyst: NonNullList<Ingredient>,
    val time: Int,
    val mruPerTick: Int,
    private val result: ItemStack
): Recipe<SimpleContainer> {
    override fun matches(
        container: SimpleContainer,
        level: Level
    ): Boolean {
        (0 ..< container.containerSize).forEach {
            if (it < 4) {
                val item = container.getItem(it)
                if (!this.inputs[it].test(item)) return false
            } else if (it == 4) {
                return this.catalyst[0].test(container.getItem(it))
            }
        }

        return true
    }

    override fun assemble(
        container: SimpleContainer,
        registryAccess: RegistryAccess
    ): ItemStack = this.result.copy()

    override fun canCraftInDimensions(width: Int, height: Int): Boolean = true

    override fun getResultItem(registryAccess: RegistryAccess): ItemStack = this.result

    override fun getId(): ResourceLocation = this.id

    override fun getIngredients(): NonNullList<Ingredient?> {
        val list = NonNullList.withSize(5, Ingredient.EMPTY)

        (0 ..< 4).forEach { list[it] = inputs[it] }

        list[4] = catalyst[0]

        return list
    }

    override fun isSpecial(): Boolean = true

    override fun getSerializer(): RecipeSerializer<*> = serial

    override fun getType(): RecipeType<*> = type

    class Serializer(
        val serial: (ResourceLocation, NonNullList<Ingredient>, NonNullList<Ingredient>, Int, Int, ItemStack) -> XLikeRecipe
    ): RecipeSerializer<XLikeRecipe> {
        override fun fromJson(recipeId: ResourceLocation, serializedRecipe: JsonObject): XLikeRecipe {
            val inputs = NonNullList.withSize(4, Ingredient.EMPTY)

            if (serializedRecipe.has("ingredients")) {
                if (!serializedRecipe.get("ingredients").isJsonArray) throw JsonSyntaxException("Recipe cannot be created, \"ingredients\" may be only as JsonArray.")
                val input = GsonHelper.getAsJsonArray(serializedRecipe, "ingredients")

                (0 ..< input.size()).forEach { i ->
                    val ingr = Ingredient.fromJson(input[i])
                    inputs[i] = ingr
                }
            }

            if (!serializedRecipe.has("catalyst")) throw JsonSyntaxException("Recipe cannot be created, because argument \"catalyst\" is missing.")
            val catalyst = GsonHelper.getAsJsonObject(serializedRecipe, "catalyst")
            val catal = NonNullList.withSize(1, Ingredient.EMPTY)
            catal[0] = Ingredient.fromJson(catalyst)

            if (!serializedRecipe.has("result")) throw JsonSyntaxException("Recipe cannot be created, because argument \"result\" is missing.")
            val result = ShapedRecipe.itemStackFromJson(serializedRecipe.getAsJsonObject("result"))

            val time = GsonHelper.getAsInt(serializedRecipe, "time", 100)
            val pseudoEnergy = GsonHelper.getAsInt(serializedRecipe, "mru", 10)

            return serial(recipeId, inputs, catal, time, pseudoEnergy, result)
        }

        override fun fromNetwork(recipeId: ResourceLocation, buffer: FriendlyByteBuf): XLikeRecipe {
            val inputs = NonNullList.withSize(4, Ingredient.EMPTY)
            for (i in 0 ..< inputs.size) {
                inputs[i] = Ingredient.fromNetwork(buffer)
            }

            val catalyzer = NonNullList.withSize(1, Ingredient.EMPTY)
            catalyzer[0] = Ingredient.fromNetwork(buffer)

            val result = buffer.readItem()
            val time = buffer.readInt()
            val mru = buffer.readInt()

            return serial(recipeId, inputs, catalyzer, time, mru, result)
        }

        override fun toNetwork(buffer: FriendlyByteBuf, recipe: XLikeRecipe) {
            recipe.ingredients.forEach { it?.toNetwork(buffer) }
            buffer.writeItem(recipe.result)
            buffer.writeInt(recipe.time)
            buffer.writeInt(recipe.mruPerTick)
        }
    }

    class Envoyer(
        id: ResourceLocation,
        inputs: NonNullList<Ingredient>,
        catalyst: NonNullList<Ingredient>,
        time: Int,
        mruPerTick: Int,
        result: ItemStack
    ): XLikeRecipe(ECRegistry.envoyerRecipe, ECRegistry.envoyerRecipeSerial, id, inputs, catalyst, time, mruPerTick, result)

    class MagicTable(
        id: ResourceLocation,
        inputs: NonNullList<Ingredient>,
        catalyst: NonNullList<Ingredient>,
        time: Int,
        mruPerTick: Int,
        result: ItemStack
    ): XLikeRecipe(ECRegistry.magicTableRecipe, ECRegistry.magicTableRecipeSerial, id, inputs, catalyst, time, mruPerTick, result)
}