package team._0mods.ecr.common.init.registry

import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import team._0mods.ecr.LOGGER
import team._0mods.ecr.ModId
import team._0mods.ecr.api.plugin.ECRModPlugin
import team._0mods.ecr.api.plugin.ECRPlugin
import team._0mods.ecr.api.plugin.registry.PlayerMatrixTypeRegistry
import team._0mods.ecr.common.capability.PlayerMRU

@ECRPlugin(ModId)
object ECPlugin: ECRModPlugin {
    private lateinit var matrixKeys: (PlayerMRU.PlayerMatrixType) -> ResourceLocation?
    private lateinit var matrixValues: (ResourceLocation) -> PlayerMRU.PlayerMatrixType?

    fun getMatrixKey(value: PlayerMRU.PlayerMatrixType) = matrixKeys(value)
    fun getMatrixValue(key: ResourceLocation) = matrixValues(key)

    override fun onMatrixTypeRegistry(reg: PlayerMatrixTypeRegistry) {
        LOGGER.info("Core plugin was initialized")

        matrixKeys = reg::getKey
        matrixValues = reg::getValue

        reg.register("basic_matrix") { reg.makeConstructor(Component.empty(), 0.0, false) }
    }
}
