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
        reg.register("basic_matrix") { reg.makeConstructor(Component.empty(), 0.0, false) }
    }

    override fun onBookTypeRegistry(reg: BookTypeRegistry) {
        reg.register("basic", ECBookTypes.BASIC)
        reg.register("mru", ECBookTypes.MRU)
        reg.register("engineer", ECBookTypes.ENGINEER)
        reg.register("hoana", ECBookTypes.HOANA)
        reg.register("shade", ECBookTypes.SHADE)
    }
}
