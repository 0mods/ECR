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
    private val ingredient: NonNullList<Ingredient>, //for jei
    val espe: Int,
    private val result: ItemStack
): Recipe<SimpleContainer> {
    override fun matches(container: SimpleContainer, level: Level): Boolean {
        if (level.isClientSide) return false

        return ingredients[0].test(container.getItem(0))
    }

    override fun assemble(inv: SimpleContainer): ItemStack = result.copy()

    override fun canCraftInDimensions(width: Int, height: Int): Boolean = true

    override fun getResultItem(): ItemStack = this.result

    override fun getId(): ResourceLocation = recipeId

    override fun getIngredients(): NonNullList<Ingredient> {
        return ingredient
    }

    override fun getSerializer(): RecipeSerializer<*> = ECRegistry.mithrilineFurnaceRecipeSerial.get()

    override fun getType(): RecipeType<*> = ECRegistry.mithrilineFurnaceRecipe.get()

    class Serializer(val serial: (ResourceLocation, NonNullList<Ingredient>, Int, ItemStack) -> MithrilineFurnaceRecipe): RecipeSerializer<MithrilineFurnaceRecipe> {
        override fun fromJson(recipeId: ResourceLocation, serializedRecipe: JsonObject): MithrilineFurnaceRecipe {
            if (!serializedRecipe.has("ingredient")) throw JsonSyntaxException("Recipe can not be created, because argument \"ingredient\" is missing.")
            val input = GsonHelper.getAsJsonObject(serializedRecipe, "ingredient")
            val i = Ingredient.fromJson(input)

            val ils = NonNullList.withSize(1, Ingredient.EMPTY)

            if (!serializedRecipe.has("result")) throw JsonSyntaxException("Recipe can not be created, because argument \"result\" is missing.")
            val result = ShapedRecipe.itemStackFromJson(serializedRecipe.getAsJsonObject("result"))

            val mru = GsonHelper.getAsInt(serializedRecipe, "espe", 100)

            ils[0] = i

            return serial(recipeId, ils, mru, result)
        }

        override fun fromNetwork(recipeId: ResourceLocation, buffer: FriendlyByteBuf): MithrilineFurnaceRecipe {
            val ingrs = NonNullList.withSize(buffer.readInt(), Ingredient.EMPTY)
            val mrusu = buffer.readInt()

            for (i in 0 ..< ingrs.size) {
                ingrs.add(i, Ingredient.fromNetwork(buffer))
            }

            val result = buffer.readItem()
            return serial(recipeId, ingrs, mrusu, result)
        }

        override fun toNetwork(buffer: FriendlyByteBuf, recipe: MithrilineFurnaceRecipe) {
            buffer.writeInt(recipe.ingredients.size)
            buffer.writeInt(recipe.espe)

            recipe.ingredients.forEach {
                it.toNetwork(buffer)
            }

            buffer.writeItem(recipe.resultItem)
        }
    }
}
