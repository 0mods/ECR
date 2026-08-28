package com.algorithmlx.ecr.neoforge.init.registry

import com.algorithmlx.ecr.api.ModId
import com.algorithmlx.ecr.common.init.ECRModIDs
import com.algorithmlx.ecr.common.recipe.MagicTableRecipe
import com.algorithmlx.ecr.common.recipe.MithrilineFurnaceRecipe
import com.algorithmlx.ecr.common.recipe.StructureRecipe
import com.algorithmlx.ecr.registry.RecipeDisplayTypeRegistry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.crafting.display.RecipeDisplay
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister

class NeoForgeRecipeDisplayTypeRegistry(
    bus: IEventBus,
) : RecipeDisplayTypeRegistry {
    private val registry = DeferredRegister.create(BuiltInRegistries.RECIPE_DISPLAY, ModId)

    init {
        registry.register(bus)
    }

    override val mithrilineFurnace: RecipeDisplay.Type<MithrilineFurnaceRecipe.Display> by register(ECRModIDs.MITHRILINE_FURNACE) { RecipeDisplay.Type(MithrilineFurnaceRecipe.Display.MAP_CODEC, MithrilineFurnaceRecipe.Display.STREAM_CODEC) }
    override val structure: RecipeDisplay.Type<StructureRecipe.Display> by register(ECRModIDs.STRUCTURE) { RecipeDisplay.Type(StructureRecipe.Display.MAP_CODEC, StructureRecipe.Display.STREAM_CODEC) }
    override val magicTable: RecipeDisplay.Type<MagicTableRecipe.Display> by register(ECRModIDs.MAGIC_TABLE) { RecipeDisplay.Type(MagicTableRecipe.Display.MAP_CODEC, MagicTableRecipe.Display.STREAM_CODEC) }

    private fun <T: RecipeDisplay> register(id: String, factory: () -> RecipeDisplay.Type<T>) = registry.register(id) { _ -> factory() }
}
