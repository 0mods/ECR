package team._0mods.ecr.common.recipes

import com.google.gson.JsonObject
import com.google.gson.JsonSyntaxException
import net.minecraft.core.NonNullList
import net.minecraft.core.RegistryAccess
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.GsonHelper
import net.minecraft.world.SimpleContainer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.*
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import ru.hollowhorizon.hc.common.utils.rl
import ru.hollowhorizon.hc.common.multiblock.Multiblock
import ru.hollowhorizon.hc.common.registry.MultiblockRegistry
import team._0mods.ecr.common.init.registry.ECRRegistry

class StructureRecipe(
    private val id: ResourceLocation,
    private val inputs: NonNullList<Ingredient>,
    val multiblock: Multiblock,
    val time: Int,
    val result: ItemStack,
    val minChance: Int,
    val maxChance: Int,
    val blockForPlace: Block?
): Recipe<SimpleContainer> {
    override fun matches(container: SimpleContainer, level: Level): Boolean {
        if (level.isClientSide) return false

        val item = container.getItem(0)

        return ingredients[0].test(item)
    }

    override fun assemble(container: SimpleContainer, registryAccess: RegistryAccess): ItemStack = this.result.copy()

    override fun canCraftInDimensions(width: Int, height: Int): Boolean = true

    override fun getResultItem(registryAccess: RegistryAccess): ItemStack = this.result

    override fun getId(): ResourceLocation = this.id

    override fun getSerializer(): RecipeSerializer<*> = ECRRegistry.structureRecipeSerial

    override fun getType(): RecipeType<*> = ECRRegistry.structureRecipe

    override fun isSpecial(): Boolean = true

    override fun getIngredients(): NonNullList<Ingredient> = this.inputs

    class Serializer(val serial: (ResourceLocation, NonNullList<Ingredient>, Multiblock, Int, ItemStack, Int, Int, Block?) -> StructureRecipe): RecipeSerializer<StructureRecipe> {
        override fun fromJson(recipeId: ResourceLocation, serializedRecipe: JsonObject): StructureRecipe {
            if (!serializedRecipe.has("ingredient")) throw JsonSyntaxException("Recipe cannot be created, because argument \"ingredient\" is missing.")
            if (serializedRecipe.get("ingredient").isJsonArray) throw JsonSyntaxException("Recipe cannot be created, because argument \"ingredient\" is array.")
            val inputJson = GsonHelper.getAsJsonObject(serializedRecipe, "ingredient")
            val input = Ingredient.fromJson(inputJson)

            val ingredients = NonNullList.withSize(1, Ingredient.EMPTY)
            if (!serializedRecipe.has("result") && !serializedRecipe.has("placement")) throw JsonSyntaxException("Recipe cannot be created, because argument \"result\" or \"placement\" is missing.")
            if (serializedRecipe.has("result") && serializedRecipe.has("placement")) throw JsonSyntaxException("Recipe cannot be created, because recipe contains arguments \"result\" and \"placement\".")
            val result = if (serializedRecipe.has("result"))
                ShapedRecipe.itemStackFromJson(serializedRecipe.getAsJsonObject("result"))
            else ItemStack.EMPTY

            val block = if (serializedRecipe.has("placement")) {
                val blockIdJson = serializedRecipe.get("placement")
                if (!blockIdJson.isJsonPrimitive) throw JsonSyntaxException("Recipe cannot be created, because argument \"placement\" is not json primitive.")
                val id = blockIdJson.asString
                if (!BuiltInRegistries.BLOCK.containsKey(id.rl)) throw JsonSyntaxException("Recipe cannot be created, because block \"$id\" is not loaded.")
                BuiltInRegistries.BLOCK.get(id.rl)
            } else null

            if (!serializedRecipe.has("multiblock")) throw JsonSyntaxException("Recipe cannot be created, because argument \"multiblock\" is missing.")
            if (!serializedRecipe.get("multiblock").isJsonPrimitive) throw JsonSyntaxException("Recipe cannot be created, because argument \"multiblock\" is not json primitive.")
            val mbId = serializedRecipe.getAsJsonPrimitive("multiblock")

            if (!mbId.isString) throw JsonSyntaxException("Recipe cannot be created, because argument \"multiblock\" is not string.")

            val multiblock = if (MultiblockRegistry.contains(mbId.asString.rl))
                MultiblockRegistry[mbId.asString.rl]
            else throw JsonSyntaxException("Recipe cannot be created, because entered multiblock is not registered.")

            val time = GsonHelper.getAsInt(serializedRecipe, "time", 0)

            if (serializedRecipe.has("chance") && !serializedRecipe.get("chance").isJsonObject) throw JsonSyntaxException("Recipe cannot be created, because argument \"chance\" is not json object.")

            val chance = if (serializedRecipe.has("chance")) {
                val jobj = serializedRecipe.get("chance").asJsonObject

                if (!(jobj.has("min") || jobj.has("max"))) throw JsonSyntaxException("Recipe cannot be created, because argument \"chance\" is not have min or max value.")

                jobj.get("min").asInt to jobj.get("max").asInt
            } else 0 to 0

            ingredients[0] = input

            return serial(recipeId, ingredients, multiblock, time, result, chance.first, chance.second, block)
        }

        override fun fromNetwork(recipeId: ResourceLocation, buffer: FriendlyByteBuf): StructureRecipe {
            val ingredients = NonNullList.withSize(buffer.readInt(), Ingredient.EMPTY)

            val tag = buffer.readNbt() ?: throw JsonSyntaxException("Network decoding failed: NBT is not present")

            val multiblock = MultiblockRegistry[tag.getString("MultiBlockId").rl]

            val blockId = if (tag.contains("BlockId")) tag.getString("BlockId") else null
            val block = blockId?.rl?.let { BuiltInRegistries.BLOCK.get(it) }

            (0 ..< ingredients.size).forEach {
                ingredients.add(it, Ingredient.fromNetwork(buffer))
            }

            val time = buffer.readInt()
            val result = buffer.readItem()

            val min = buffer.readInt()
            val max = buffer.readInt()

            return serial(recipeId, ingredients, multiblock, time, result, min, max, block)
        }

        override fun toNetwork(buffer: FriendlyByteBuf, recipe: StructureRecipe) {
            val tag = CompoundTag().apply {
                this.putString("MultiBlockId", MultiblockRegistry[recipe.multiblock].toString())
                recipe.blockForPlace?.let { try { BuiltInRegistries.BLOCK.getKey(it) } catch (e: Exception) { null }?.let { i -> this.putString("BlockId", i.toString()) } }
            }

            buffer.writeInt(recipe.ingredients.size)
            buffer.writeNbt(tag)

            recipe.ingredients.forEach {
                it.toNetwork(buffer)
            }

            buffer.writeInt(recipe.time)
            buffer.writeItem(recipe.result)
            buffer.writeInt(recipe.minChance)
            buffer.writeInt(recipe.maxChance)
        }
    }
}
