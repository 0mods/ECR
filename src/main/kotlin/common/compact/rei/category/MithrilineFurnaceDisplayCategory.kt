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
import team._0mods.ecr.common.compact.rei.MITHRILINE_FURNACE_DISPLAY
import team._0mods.ecr.common.compact.rei.display.MithrilineFurnaceDisplay
import team._0mods.ecr.common.compact.rei.widget.LittleArrowWidget
import team._0mods.ecr.common.init.registry.ECRRegistry

object MithrilineFurnaceDisplayCategory: DisplayCategory<MithrilineFurnaceDisplay> {
    private val block = ECRRegistry.mithrilineFurnace

    override fun getCategoryIdentifier(): CategoryIdentifier<out MithrilineFurnaceDisplay?>? =
        MITHRILINE_FURNACE_DISPLAY

    override fun getTitle(): Component? = block.name

    override fun getIcon(): Renderer? = EntryStacks.of(block)

    override fun setupDisplay(display: MithrilineFurnaceDisplay, bounds: Rectangle): List<Widget?>? {
        val x = bounds.centerX - 8
        val y = bounds.centerY - 26

        val widgets = mutableListOf<Widget>()
        widgets += Widgets.createRecipeBase(bounds)
        widgets += Widgets.createSlot(Point(x, y)).entries(display.inputEntries[0])
        widgets += Widgets.createSlot(Point(x, y + 36)).entries(display.outputEntries[0])
        widgets += LittleArrowWidget.createVertical(Point(x + 5, y + 18))

        return widgets
    }
}