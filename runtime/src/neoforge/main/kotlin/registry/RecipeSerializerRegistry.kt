package com.algorithmlx.ecr.registry

import com.algorithmlx.ecr.api.ModId
import com.algorithmlx.ecr.common.init.ECRModIDs
import com.algorithmlx.ecr.common.recipe.*
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.crafting.RecipeSerializer
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister

@Suppress("ACTUAL_WITHOUT_EXPECT", "unused")
actual object RecipeSerializerRegistry {
    private val recipeSerializers = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, ModId)

    fun init(bus: IEventBus) {
        recipeSerializers.register(bus)
    }

    private val mithrilineFurnaceSerializer = recipeSerializers.register(ECRModIDs.MITHRILINE_FURNACE) { _ ->
        RecipeSerializer(MithrilineFurnaceRecipe.CODEC, MithrilineFurnaceRecipe.STREAM_CODEC)
    }
    private val structureRecipe = recipeSerializers.register(ECRModIDs.STRUCTURE) { _ ->
        RecipeSerializer(StructureRecipe.CODEC, StructureRecipe.STREAM_CODEC)
    }
    private val magicTableRecipe = recipeSerializers.register(ECRModIDs.MAGIC_TABLE) { _ ->
        RecipeSerializer(MagicTableRecipe.CODEC, MagicTableRecipe.STREAM_CODEC)
    }

    actual val mithrilineFurnace: RecipeSerializer<MithrilineFurnaceRecipe> by lazy { mithrilineFurnaceSerializer.get() }
    actual val structure: RecipeSerializer<StructureRecipe> by lazy { structureRecipe.get() }
    actual val magicTable: RecipeSerializer<MagicTableRecipe> by lazy { magicTableRecipe.get() }
}
