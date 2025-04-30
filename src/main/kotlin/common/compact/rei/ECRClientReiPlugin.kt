package team._0mods.ecr.common.compact.rei

import me.shedaniel.rei.api.client.plugins.REIClientPlugin
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry
import me.shedaniel.rei.api.common.util.EntryStacks
import ru.hollowhorizon.hc.common.objects.recipe.ingredient.DefaultHollowIngredient
import team._0mods.ecr.common.compact.rei.category.XLikeDisplayCategory
import team._0mods.ecr.common.compact.rei.display.MithrilineFurnaceDisplay
import team._0mods.ecr.common.compact.rei.display.XLikeDisplay
import team._0mods.ecr.common.init.registry.ECRRegistry
import team._0mods.ecr.common.recipes.XLikeRecipe

class ECRClientReiPlugin: REIClientPlugin {
    override fun registerCategories(registry: CategoryRegistry?) {
        registry?.add(XLikeDisplayCategory.Envoyer)
        registry?.addWorkstations(ENVOYER_DISPLAY, EntryStacks.of(ECRRegistry.envoyer))

        registry?.add(XLikeDisplayCategory.MagicTable)
        registry?.addWorkstations(MAGIC_TABLE_DISPLAY, EntryStacks.of(ECRRegistry.magicTable))

        registry?.addWorkstations(MITHRILINE_FURNACE_DISPLAY, EntryStacks.of(ECRRegistry.mithrilineFurnace))
    }

    override fun registerDisplays(registry: DisplayRegistry?) {
        registry?.registerFiller(XLikeRecipe.Envoyer::class.java) { XLikeDisplay.Envoyer(it) }
        registry?.registerFiller(XLikeRecipe.MagicTable::class.java) { XLikeDisplay.MagicTable(it) }
    }
}