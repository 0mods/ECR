package team._0mods.ecr.client.screen

import imgui.flag.ImGuiWindowFlags
import ru.hollowhorizon.hc.client.imgui.setWindowSize
import ru.hollowhorizon.hc.client.screens.ImGuiScreen
import team._0mods.ecr.common.items.ECBook

class ECBookScreen(private val type: ECBook.Type): ImGuiScreen({
    centredWindow("No Title?", args = ImGuiWindowFlags.AlwaysAutoResize or ImGuiWindowFlags.NoMove) {
        setWindowSize(1200f, 500f)

    }
}) {
    override fun isPauseScreen(): Boolean = false
}
