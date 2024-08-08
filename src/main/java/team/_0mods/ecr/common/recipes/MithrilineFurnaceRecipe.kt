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

class MithrilineFurnaceRecipe(
    private val recipeId: ResourceLocation,
    private val ingredient: Ingredient,
    val mrusu: Int,
    private val result: ItemStack
): Recipe<SimpleContainer> {
    override fun matches(container: SimpleContainer, level: Level): Boolean {
        if (level.isClientSide) return false

        return ingredient.test(container.getItem(0))
    }

    override fun assemble(inv: SimpleContainer): ItemStack = result.copy()

    override fun canCraftInDimensions(width: Int, height: Int): Boolean = true

    override fun getResultItem(): ItemStack = this.result

    override fun getId(): ResourceLocation = recipeId

    override fun getIngredients(): NonNullList<Ingredient> {
        return NonNullList.of(this.ingredient)
    }

    override fun getSerializer(): RecipeSerializer<*> = ECRegistry.mithrilineFurnaceRecipeSerial.get()

    override fun getType(): RecipeType<*> = ECRegistry.mithrilineFurnaceRecipe.get()

    class Serializer(val serial: (ResourceLocation, Ingredient, Int, ItemStack) -> MithrilineFurnaceRecipe): RecipeSerializer<MithrilineFurnaceRecipe> {
        override fun fromJson(recipeId: ResourceLocation, serializedRecipe: JsonObject): MithrilineFurnaceRecipe {
            if (!serializedRecipe.has("ingredient")) throw JsonSyntaxException("Recipe can not be created, because argument \"ingredient\" is missing.")
            val input = GsonHelper.getAsJsonObject(serializedRecipe, "ingredient")
            val i = Ingredient.fromJson(input)

            if (!serializedRecipe.has("result")) throw JsonSyntaxException("Recipe can not be created, because argument \"result\" is missing.")
            val result = ShapedRecipe.itemStackFromJson(serializedRecipe.getAsJsonObject("result"))

            val mru = GsonHelper.getAsInt(serializedRecipe, "mrusu", 100)

            return serial(recipeId, i, mru, result)
        }

        override fun fromNetwork(recipeId: ResourceLocation, buffer: FriendlyByteBuf): MithrilineFurnaceRecipe {
            val i = Ingredient.fromNetwork(buffer)
            val mrusu = buffer.readInt()
            val result = buffer.readItem()
            return serial(recipeId, i, mrusu, result)
        }

        override fun toNetwork(buffer: FriendlyByteBuf, recipe: MithrilineFurnaceRecipe) {
            recipe.ingredient.toNetwork(buffer)
            buffer.writeInt(recipe.mrusu)
            buffer.writeItem(recipe.resultItem)
        }
    }
}