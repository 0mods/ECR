package team._0mods.ecr.common.compact.rei.category

import me.shedaniel.math.Rectangle
import me.shedaniel.rei.api.client.gui.Renderer
import me.shedaniel.rei.api.client.gui.widgets.Widget
import me.shedaniel.rei.api.client.registry.display.DisplayCategory
import me.shedaniel.rei.api.common.category.CategoryIdentifier
import me.shedaniel.rei.api.common.util.EntryStacks
import net.minecraft.network.chat.Component
import team._0mods.ecr.common.compact.rei.ENVOYER
import team._0mods.ecr.common.compact.rei.display.EnvoyerDisplay
import team._0mods.ecr.common.init.registry.ECRegistry

object EnvoyerDisplayCategory: DisplayCategory<EnvoyerDisplay> {
    override fun getCategoryIdentifier(): CategoryIdentifier<out EnvoyerDisplay?>? = ENVOYER

    override fun getTitle(): Component? = ECRegistry.envoyer.name

    override fun getIcon(): Renderer? = EntryStacks.of(ECRegistry.envoyer)

    override fun setupDisplay(display: EnvoyerDisplay?, bounds: Rectangle?): List<Widget?>? {
        // todo: write render for rei
        return super.setupDisplay(display, bounds)
    }
}