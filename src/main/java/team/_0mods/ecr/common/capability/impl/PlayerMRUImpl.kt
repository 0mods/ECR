package team._0mods.ecr.common.capability.impl

import team._0mods.ecr.ECCoroutine
import team._0mods.ecr.ModId
import team._0mods.ecr.api.mru.player.PlayerMatrixType
import team._0mods.ecr.api.rl
import team._0mods.ecr.common.capability.PlayerMRU
import team._0mods.ecr.common.init.registry.ECCorePlugin

class PlayerMRUImpl: PlayerMRU {
    override var matrixDestruction: Double = 0.0
    override var matrixType: PlayerMatrixType = ECCorePlugin.playerMatrixTypes["$ModId:basic_matrix".rl]!!
}