package team._0mods.ecr.common.compact.rei.category

import me.shedaniel.math.Point
import me.shedaniel.math.Rectangle
import me.shedaniel.rei.api.client.gui.Renderer
import me.shedaniel.rei.api.client.gui.widgets.Widget
import me.shedaniel.rei.api.client.gui.widgets.Widgets
import me.shedaniel.rei.api.client.registry.display.DisplayCategory
import me.shedaniel.rei.api.common.category.CategoryIdentifier
import me.shedaniel.rei.api.common.util.EntryStacks
import net.minecraft.network.chat.Component
import net.minecraft.world.level.block.Block
import team._0mods.ecr.common.compact.rei.ENVOYER_DISPLAY
import team._0mods.ecr.common.compact.rei.MAGIC_TABLE_DISPLAY
import team._0mods.ecr.common.compact.rei.display.XLikeDisplay
import team._0mods.ecr.common.compact.rei.widget.LittleArrowWidget
import team._0mods.ecr.common.compact.rei.widget.MRULineWidget
import team._0mods.ecr.common.init.registry.ECRRegistry

abstract class XLikeDisplayCategory<T: XLikeDisplay>(
    private val block: Block
): DisplayCategory<T> {
    override fun getTitle(): Component? = block.name

    override fun getIcon(): Renderer? = EntryStacks.of(block)

    override fun setupDisplay(display: T, bounds: Rectangle): List<Widget> {
        // width = 150
        // height = 66
        val centerX = bounds.centerX
        val centerY = bounds.centerY
        val x = centerX - 64
        val y = centerY - 26

        val widgets = mutableListOf<Widget>()
        widgets += Widgets.createRecipeBase(bounds)
        widgets += MRULineWidget.createWidget(Point(x + 72, y), display.mru * display.time, display.time)
        widgets += LittleArrowWidget.createHorizontal(Point(x + 60, y + 23))

        val slotPositions = listOf(
            Point(x, y),
            Point(x + 36, y),
            Point(x, y + 36),
            Point(x + 36, y + 36),

            Point(x + 18, y + 18)
        )

        if (display.inputEntries.size != 1) {
            for (i in 0..4) {
                widgets += Widgets.createSlot(slotPositions[i]).apply {
                    display.inputEntries.getOrNull(i)?.let { entries(it) }
                }
            }
        } else {
            for (i in 0..3) {
                widgets += Widgets.createSlot(slotPositions[i])
            }

            widgets += Widgets.createSlot(slotPositions[4]).entries(display.inputEntries[0])
        }

        val output = display.outputEntries[0]

        widgets += Widgets.createSlot(Point(x + 90, y + 18)).entries(output)

        return widgets
    }

    object Envoyer: XLikeDisplayCategory<XLikeDisplay.Envoyer>(ECRRegistry.envoyer) {
        override fun getCategoryIdentifier(): CategoryIdentifier<out XLikeDisplay.Envoyer?>? = ENVOYER_DISPLAY
    }

    object MagicTable: XLikeDisplayCategory<XLikeDisplay.MagicTable>(ECRRegistry.magicTable) {
        override fun getCategoryIdentifier(): CategoryIdentifier<out XLikeDisplay.MagicTable?>? = MAGIC_TABLE_DISPLAY
    }
}
