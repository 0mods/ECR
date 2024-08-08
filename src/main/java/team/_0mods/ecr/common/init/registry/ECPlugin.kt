package team._0mods.ecr.common.init.registry

import net.minecraft.network.chat.Component
import team._0mods.ecr.LOGGER
import team._0mods.ecr.ModId
import team._0mods.ecr.api.plugin.ECRModPlugin
import team._0mods.ecr.api.plugin.ECRPlugin
import team._0mods.ecr.api.plugin.registry.PlayerMatrixTypeRegistry

@ECRPlugin(ModId)
class ECPlugin: ECRModPlugin {
    override fun onMatrixTypeRegistry(reg: PlayerMatrixTypeRegistry) {
        LOGGER.info("Core plugin was initialized")

        reg.register("basic_matrix") {
            reg.makeConstructor(Component.empty(), 0.0, false)
        }
    }
}