package team._0mods.ecr.common.capability

import team._0mods.ecr.api.mru.player.PlayerMatrixType

interface PlayerMRU {
    var matrixDestruction: Double

    var matrixType: PlayerMatrixType

    var isInfused: Boolean
}