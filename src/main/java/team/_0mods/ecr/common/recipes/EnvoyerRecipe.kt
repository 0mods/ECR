package team._0mods.ecr.common.recipes

import com.google.gson.JsonObject
import com.google.gson.JsonSyntaxException
import net.minecraft.core.NonNullList
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.GsonHelper
import net.minecraft.world.SimpleContainer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.*
import net.minecraft.world.level.Level
import team._0mods.ecr.common.init.registry.ECRegistry

class EnvoyerRecipe(
    private val id: ResourceLocation,
    private val inputs: NonNullList<Ingredient>,
    private val catalyzer: NonNullList<Ingredient>,
    val time: Int,
    val mruPerTick: Int,
    private val result: ItemStack
): Recipe<SimpleContainer> {
    override fun matches(container: SimpleContainer, level: Level): Boolean {
        for (i in 0 ..< container.containerSize) {
            if (i < 5) {
                val item = container.getItem(i)
                if (!inputs[i].test(item)) return false
            } else if (i == 5) {
                return catalyzer[0].test(container.getItem(i))
            }
        }

        return true
    }

    override fun assemble(container: SimpleContainer): ItemStack = result.copy()

    override fun canCraftInDimensions(width: Int, height: Int): Boolean = true

    override fun getResultItem(): ItemStack = result

    override fun getId(): ResourceLocation = id

    override fun getSerializer(): RecipeSerializer<*> = ECRegistry.envoyerRecipeSerial.get()

    override fun getType(): RecipeType<*> = ECRegistry.envoyerRecipe.get()

    override fun isSpecial(): Boolean = true

    class Serializer(
        val serial: (ResourceLocation, NonNullList<Ingredient>, NonNullList<Ingredient>, Int, Int, ItemStack) -> EnvoyerRecipe
    ): RecipeSerializer<EnvoyerRecipe> {
        override fun fromJson(recipeId: ResourceLocation, serializedRecipe: JsonObject): EnvoyerRecipe {
            if (!serializedRecipe.has("ingredients")) throw JsonSyntaxException("Recipe cannot be created, because argument \"ingredients\" is missing.")
            if (!serializedRecipe.get("ingredients").isJsonArray) throw JsonSyntaxException("Recipe cannot be created, \"ingredients\" may be only as JsonArray.")
            val input = GsonHelper.getAsJsonArray(serializedRecipe, "ingredients")
            val inputs = NonNullList.withSize(4, Ingredient.EMPTY)

            input.forEachIndexed { index, it ->
                val i = Ingredient.fromJson(it)
                inputs[index] = i
            }

            if (!serializedRecipe.has("catalyzer"))  throw JsonSyntaxException("Recipe cannot be created, because argument \"catalyzer\" is missing.")
            val catalyzer = GsonHelper.getAsJsonObject(serializedRecipe, "catalyzer")
            val catal = NonNullList.withSize(1, Ingredient.EMPTY)
            catal[0] = Ingredient.fromJson(catalyzer)

            if (!serializedRecipe.has("result")) throw JsonSyntaxException("Recipe cannot be created, because argument \"result\" is missing.")
            val result = ShapedRecipe.itemStackFromJson(serializedRecipe.getAsJsonObject("result"))

            val time = GsonHelper.getAsInt(serializedRecipe, "time", 100)
            val pseudoEnergy = GsonHelper.getAsInt(serializedRecipe, "mru", 10)

            return serial(recipeId, inputs, catal, time, pseudoEnergy, result)
        }

        override fun fromNetwork(recipeId: ResourceLocation, buffer: FriendlyByteBuf): EnvoyerRecipe {
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

        override fun toNetwork(buffer: FriendlyByteBuf, recipe: EnvoyerRecipe) {
            recipe.inputs.forEach { it.toNetwork(buffer) }
            recipe.catalyzer.forEach { it.toNetwork(buffer) }
            buffer.writeItem(recipe.result)
            buffer.writeInt(recipe.time)
            buffer.writeInt(recipe.mruPerTick)
        }
    }
}
