package team._0mods.ecr.common.api.registry

import team._0mods.ecr.api.mru.PlayerMatrixType
import team._0mods.ecr.api.plugin.registry.PlayerMatrixTypeRegistry
import team._0mods.ecr.api.plugin.registry.helper.RegistryImplementer
import team._0mods.ecr.api.registries.ECRegistries

internal class InternalPlayerMatrixTypeRegistry(modId: String):
    RegistryImplementer<PlayerMatrixType>(modId, ECRegistries.PLAYER_MATRICES),
    PlayerMatrixTypeRegistry
