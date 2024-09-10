package team._0mods.ecr.common.init.registry

import net.minecraft.network.chat.Component
import team._0mods.ecr.ModId
import team._0mods.ecr.api.plugin.ECRModPlugin
import team._0mods.ecr.api.plugin.ECRPlugin
import team._0mods.ecr.api.plugin.registry.BookTypeRegistry
import team._0mods.ecr.api.plugin.registry.PlayerMatrixTypeRegistry

@ECRPlugin(ModId)
object ECPlugin: ECRModPlugin {
    override fun onMatrixTypeRegistry(reg: PlayerMatrixTypeRegistry) {
        reg.register("basic_matrix") { reg.makeConstructor(Component.literal("Basic Matrix"), 0.0, false) }
    }

    override fun onBookTypeRegistry(reg: BookTypeRegistry) {
        ECBookTypes.entries.forEach {
            val id = it.name.lowercase()
            reg.register(id, it)
        }
    }
}
