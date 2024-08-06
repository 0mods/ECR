package team._0mods.ecr.api.plugin

import team._0mods.ecr.api.plugin.registry.PlayerMatrixTypeRegistry

annotation class ECRPlugin(val modId: String)

interface ECRModPlugin {
    fun onMatrixTypeRegistry(reg: PlayerMatrixTypeRegistry)
}