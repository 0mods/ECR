@file:JvmName("ReiRecipes")
package team._0mods.ecr.common.compact.rei

import me.shedaniel.rei.api.common.category.CategoryIdentifier
import team._0mods.ecr.api.ModId
import team._0mods.ecr.common.compact.rei.display.EnvoyerDisplay

@JvmField val ENVOYER: CategoryIdentifier<EnvoyerDisplay> = CategoryIdentifier.of(ModId, "plugins/envoyer")
