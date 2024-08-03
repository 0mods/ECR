package team._0mods.ecr.common.recipes

import com.google.gson.JsonObject
import net.minecraft.core.NonNullList
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.GsonHelper
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.item.crafting.ShapedRecipe
import net.minecraftforge.items.IItemHandler
import team._0mods.ecr.common.init.registry.ECRecipeTypes
import team._0mods.ecr.common.init.registry.ECRegistry
import team._0mods.ecr.common.utils.recipe.ForgefiedRecipe

class MithrilineFurnaceRecipe(
    private val recipeId: ResourceLocation,
    private val ingredient: Ingredient,
    val mrusu: Int,
    private val result: ItemStack
): ForgefiedRecipe {
    override fun assemble(inv: IItemHandler): ItemStack = result.copy()

    override fun canCraftInDimensions(width: Int, height: Int): Boolean = true

    override fun getResultItem(): ItemStack = this.result

    override fun getId(): ResourceLocation = recipeId

    override fun getIngredients(): NonNullList<Ingredient> {
        return NonNullList.of(this.ingredient)
    }

    override fun getSerializer(): RecipeSerializer<*> = ECRegistry.mithrilineFurnaceRecipe.get()

    override fun getType(): RecipeType<*> = ECRecipeTypes.mithrilineFurnace

    class Serializer(val serial: (ResourceLocation, Ingredient, Int, ItemStack) -> MithrilineFurnaceRecipe): RecipeSerializer<MithrilineFurnaceRecipe> {
        override fun fromJson(recipeId: ResourceLocation, serializedRecipe: JsonObject): MithrilineFurnaceRecipe {
            val input = GsonHelper.getAsJsonObject(serializedRecipe, "in")
            val i = Ingredient.fromJson(input)
            val result = ShapedRecipe.itemStackFromJson(serializedRecipe.getAsJsonObject("out"))
            val mru = GsonHelper.getAsInt(serializedRecipe, "mrusu")
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