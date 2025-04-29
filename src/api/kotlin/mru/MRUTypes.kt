package team._0mods.ecr.api.mru

import kotlinx.serialization.Serializable
import net.minecraft.network.chat.Component

@Serializable
enum class MRUTypes(val displayName: Component) {
    RADIATION_UNIT(Component.literal("MRU")),
    ESPE(Component.literal("ESPE"))
}
