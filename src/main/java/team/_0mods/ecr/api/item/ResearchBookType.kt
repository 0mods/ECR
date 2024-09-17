package team._0mods.ecr.api.item

import net.minecraft.network.chat.Component
import ru.hollowhorizon.hc.client.imgui.Graphics
import team._0mods.ecr.api.registries.ECRegistries

interface ResearchBookType {
    /**
     * Type display name in tooltip & GUI
     * @return [Component] of name
     */
    val translate: Component
        get() {
            val registry = ECRegistries.BOOK_TYPES.getKey(this)
            if (registry == null) throw NullPointerException("You try to load display name content for null or not registered book type.")
            return Component.translatable("book_type.${registry.namespace}.${registry.path}")
        }

    /**
     * Render in a book.
     *
     * If empty, it can't be rendered
     *
     * @return Function with [Graphics] parameter
     */
    val render: Graphics.() -> Unit get() = {}
}
