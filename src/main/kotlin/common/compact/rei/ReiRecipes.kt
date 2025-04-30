@file:JvmName("ReiRecipes")
package team._0mods.ecr.common.compact.rei

import me.shedaniel.rei.api.common.category.CategoryIdentifier
import team._0mods.ecr.api.ModId
import team._0mods.ecr.common.compact.rei.display.MithrilineFurnaceDisplay
import team._0mods.ecr.common.compact.rei.display.XLikeDisplay

@JvmField val ENVOYER_DISPLAY: CategoryIdentifier<XLikeDisplay.Envoyer> = CategoryIdentifier.of(ModId, "plugins/envoyer")
@JvmField val MAGIC_TABLE_DISPLAY: CategoryIdentifier<XLikeDisplay.MagicTable> = CategoryIdentifier.of(ModId, "plugins/magic_table")
@JvmField val MITHRILINE_FURNACE_DISPLAY: CategoryIdentifier<MithrilineFurnaceDisplay> = CategoryIdentifier.of(ModId, "plugins/mithriline_furnace")
