package team._0mods.ecr.datagen.builder

import com.google.gson.JsonObject
import net.minecraft.advancements.CriterionTriggerInstance
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.data.recipes.FinishedRecipe
import net.minecraft.data.recipes.RecipeBuilder
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.level.ItemLike
import team._0mods.ecr.api.utils.ecRL
import team._0mods.ecr.common.init.registry.ECRegistry
import java.util.function.Consumer

class MithrilineFurnaceRecipeBuilder private constructor(private val result: ItemLike, private val count: Int): RecipeBuilder {
    companion object {
        fun make(result: ItemLike): MithrilineFurnaceRecipeBuilder = MithrilineFurnaceRecipeBuilder(result, 1)

        fun make(result: ItemLike, count: Int): MithrilineFurnaceRecipeBuilder = MithrilineFurnaceRecipeBuilder(result, count)
    }

    private val ingredient: MutableList<Ingredient> = mutableListOf()
    private var ingredientCount = 1
    private var espe = 0

    fun requires(itemLike: ItemLike, count: Int = 1): MithrilineFurnaceRecipeBuilder {
        if (ingredient.isEmpty()) ingredient += Ingredient.of(itemLike)
        this.ingredientCount = count
        return this
    }

    fun requires(ingredient: Ingredient, count: Int = 1): MithrilineFurnaceRecipeBuilder {
        if (this.ingredient.isEmpty()) this.ingredient += ingredient
        this.ingredientCount = count
        return this
    }

    fun espe(espe: Int): MithrilineFurnaceRecipeBuilder {
        this.espe = espe
        return this
    }

    override fun unlockedBy(criterionName: String, criterionTrigger: CriterionTriggerInstance): RecipeBuilder = this

    override fun group(groupName: String?): RecipeBuilder = this

    override fun getResult(): Item = this.result.asItem()

    override fun save(finishedRecipeConsumer: Consumer<FinishedRecipe>) {
        this.save(finishedRecipeConsumer, BuiltInRegistries.ITEM.getKey(this.result.asItem()).path.ecRL)
    }

    override fun save(finishedRecipeConsumer: Consumer<FinishedRecipe>, recipeId: ResourceLocation) {
        finishedRecipeConsumer.accept(Finalized(recipeId, result.asItem(), count, espe, ingredient, ingredientCount))
    }

    class Finalized(
        private val id: ResourceLocation,
        private val result: Item,
        private val count: Int,
        private val espe: Int,
        private val ingredient: List<Ingredient>,
        private val ingredientCount: Int
    ): FinishedRecipe {
        override fun serializeRecipeData(json: JsonObject) {
            val i = ingredient[0]

            if (ingredientCount == 1)
                json.add("ingredient", i.toJson())
            else {
                val jo = JsonObject().apply {
                    val item = i.items[0].item
                    this.addProperty("type", "forge:nbt")
                    this.addProperty("count", this@Finalized.ingredientCount)
                    this.addProperty("item", BuiltInRegistries.ITEM.getKey(item).toString())
                }

                json.add("ingredient", jo)
            }

            json.addProperty("espe", this.espe)

            val jo = JsonObject().apply {
                addProperty("item", BuiltInRegistries.ITEM.getKey(this@Finalized.result).toString())
                if (this@Finalized.count > 1) {
                    addProperty("count", this@Finalized.count)
                }
            }

            json.add("result", jo)
        }

        override fun getId(): ResourceLocation = this.id

        override fun getType(): RecipeSerializer<*> = ECRegistry.mithrilineFurnaceRecipeSerial

        override fun serializeAdvancement(): JsonObject? = null

        override fun getAdvancementId(): ResourceLocation? = null
    }
}