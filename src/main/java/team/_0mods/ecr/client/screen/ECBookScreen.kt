package team._0mods.ecr.client.screen

import ru.hollowhorizon.hc.client.screens.ImGuiScreen
import team._0mods.ecr.common.items.ECBook

class ECBookScreen(private val type: ECBook.Type): ImGuiScreen({
}) {
    override fun isPauseScreen(): Boolean = false
}
