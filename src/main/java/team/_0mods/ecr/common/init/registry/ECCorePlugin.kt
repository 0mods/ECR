package team._0mods.ecr.common.init.registry

import team._0mods.ecr.LOGGER
import team._0mods.ecr.ModId
import team._0mods.ecr.api.plugin.ECRModPlugin
import team._0mods.ecr.api.plugin.ECRPlugin
import team._0mods.ecr.api.plugin.registry.PlayerMatrixTypeRegistry

@ECRPlugin(ModId)
class ECCorePlugin: ECRModPlugin {
    override fun onMatrixTypeRegistry(reg: PlayerMatrixTypeRegistry) {
        LOGGER.info("Core plugin was initialized")
    }
}