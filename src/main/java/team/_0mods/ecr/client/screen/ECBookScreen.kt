package team._0mods.ecr.client.screen

import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.scene.Scene
import ru.hollowhorizon.hc.client.kool.KoolScreen
import team._0mods.ecr.api.item.ResearchBookType

class ECBookScreen(private val type: List<ResearchBookType>): KoolScreen({
    setupUiScene(Scene.DEFAULT_CLEAR_COLOR)

    addPanelSurface {
        modifier.size(400.dp, 300.dp)
            .align(AlignmentX.Center, AlignmentY.Center)
            .background(RoundRectBackground(colors.background, 16.dp))

        Text(type.last().translate.string) {
            modifier.align(AlignmentX.Center, AlignmentY.Center)
        }
    }
}) {
    override fun isPauseScreen(): Boolean = false
}
