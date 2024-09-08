package team._0mods.ecr.client.screen

import imgui.flag.ImGuiWindowFlags
import net.minecraft.client.Minecraft
import ru.hollowhorizon.hc.client.imgui.setWindowSize
import ru.hollowhorizon.hc.client.screens.ImGuiScreen
import team._0mods.ecr.api.item.ECBookType

class ECBookScreen(private val type: List<ECBookType>): ImGuiScreen({
    val sc = Minecraft.getInstance().window.guiScale.toFloat()

    centredWindow("No Title?", args = ImGuiWindowFlags.AlwaysAutoResize or ImGuiWindowFlags.NoMove or ImGuiWindowFlags.NoCollapse) {
        setWindowSize(1200f * 6, 500f * sc)

        pushScreenCursor()
        text(type.last().translate)
        popScreenCursor()

    }
}) {
    override fun isPauseScreen(): Boolean = false
}
