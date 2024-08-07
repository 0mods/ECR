package team._0mods.ecr.common.init.registry

import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import team._0mods.ecr.LOGGER
import team._0mods.ecr.ModId
import team._0mods.ecr.api.mru.player.PlayerMatrixType
import team._0mods.ecr.api.plugin.ECRModPlugin
import team._0mods.ecr.api.plugin.ECRPlugin
import team._0mods.ecr.api.plugin.registry.PlayerMatrixTypeRegistry

@ECRPlugin(ModId)
class ECCorePlugin: ECRModPlugin {
    companion object {
        lateinit var playerMatrixTypes: Map<ResourceLocation, PlayerMatrixType>
            private set

        lateinit var key: (PlayerMatrixType) -> ResourceLocation?
            private set

        lateinit var value: (ResourceLocation) -> PlayerMatrixType?
            private set
    }

    override fun onMatrixTypeRegistry(reg: PlayerMatrixTypeRegistry) {
        LOGGER.info("Core plugin was initialized")
        key = { reg.getKey(it) }
        value = { reg.getValue(it) }
        reg.register("basic_matrix", object : PlayerMatrixType {
            override val name: Component = Component.empty()
            override val reduceRadiationMultiplier: Double = 0.0
            override val protectMatrixDecay: Boolean = false
        })
        playerMatrixTypes = reg.matrixTypes
    }
}
