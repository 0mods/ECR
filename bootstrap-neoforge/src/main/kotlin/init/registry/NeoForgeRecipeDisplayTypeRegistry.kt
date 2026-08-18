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

    private val mithrilineFurnaceDisplay =
        registry.register(ECRModIDs.MITHRILINE_FURNACE) { _ ->
            RecipeDisplay.Type(MithrilineFurnaceRecipe.Display.MAP_CODEC, MithrilineFurnaceRecipe.Display.STREAM_CODEC)
        }

    private val structureDisplayDisplay =
        registry.register(ECRModIDs.STRUCTURE) { _ ->
            RecipeDisplay.Type(StructureRecipe.Display.MAP_CODEC, StructureRecipe.Display.STREAM_CODEC)
        }

    private val magicTableDisplay =
        registry.register(ECRModIDs.MAGIC_TABLE) { _ ->
            RecipeDisplay.Type(MagicTableRecipe.Display.MAP_CODEC, MagicTableRecipe.Display.STREAM_CODEC)
        }

    override val mithrilineFurnace: RecipeDisplay.Type<MithrilineFurnaceRecipe.Display> by lazy { mithrilineFurnaceDisplay.get() }
    override val structure: RecipeDisplay.Type<StructureRecipe.Display> by lazy { structureDisplayDisplay.get() }
    override val magicTable: RecipeDisplay.Type<MagicTableRecipe.Display> by lazy { magicTableDisplay.get() }
}
