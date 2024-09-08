package team._0mods.ecr.api.item

import net.minecraft.network.chat.Component
import ru.hollowhorizon.hc.client.imgui.Graphics

interface ECBookType {
    /**
     * Type display name in tooltip & GUI
     * @return [Component] of name
     */
    val translate: Component

    /**
     * Render in a book.
     *
     * If empty, it can't be rendered
     *
     * @return [Renders] delegate
     */
    val render: Renders get() = Renders {}

    fun interface Renders {
        fun render(graphics: Graphics)
    }
}
