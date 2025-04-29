package team._0mods.ecr.common.compact.rei

import me.shedaniel.rei.api.client.plugins.REIClientPlugin
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry
import team._0mods.ecr.common.compact.rei.category.EnvoyerDisplayCategory
import team._0mods.ecr.common.compact.rei.display.EnvoyerDisplay
import team._0mods.ecr.common.recipes.XLikeRecipe

class ECRClientReiPlugin: REIClientPlugin {
    override fun registerCategories(registry: CategoryRegistry?) {
        registry?.add(EnvoyerDisplayCategory)
    }

    override fun registerDisplays(registry: DisplayRegistry?) {
        registry?.registerFiller(XLikeRecipe.Envoyer::class.java) { EnvoyerDisplay(it) }
    }
}