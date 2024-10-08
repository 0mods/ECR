package team._0mods.ecr.common.init.registry

import team._0mods.ecr.api.ModId
import team._0mods.ecr.api.plugin.ECRModPlugin
import team._0mods.ecr.api.plugin.ECRPlugin
import team._0mods.ecr.api.plugin.registry.BookTypeRegistry
import team._0mods.ecr.api.plugin.registry.PlayerMatrixTypeRegistry

@ECRPlugin(ModId)
object ECPlugin: ECRModPlugin {
    override fun onMatrixTypeRegistry(reg: PlayerMatrixTypeRegistry) {
        ECPlayerMatrices.entries.forEach {
            val id = it.name.lowercase()
            reg.register(id, it)
        }
    }

    override fun onBookTypeRegistry(reg: BookTypeRegistry) {
        ECBookTypes.entries.forEach {
            val id = it.name.lowercase()
            reg.register(id, it)
        }
    }
}
