package team._0mods.ecr.api.item

import de.fabmax.kool.scene.Scene
import net.minecraft.network.chat.Component
import ru.hollowhorizon.hc.client.utils.mcTranslate
import team._0mods.ecr.api.registries.ECRegistries

interface Research {
    /**
     * Type display name in tooltip & GUI
     * @return [Component] of name
     */
    val translate: Component
        get() {
            val registry = ECRegistries.BOOK_TYPES.getKey(this)
            if (registry == null) throw NullPointerException("You try to load display name content for null or not registered book type.")
            return "book_type.${registry.namespace}.${registry.path}".mcTranslate
        }

    /**
     * Render in a book.
     *
     * If empty, it can't be rendered
     *
     * @return Function with [Scene] parameter
     */
    val render: Scene.() -> Unit get() = {}
}
