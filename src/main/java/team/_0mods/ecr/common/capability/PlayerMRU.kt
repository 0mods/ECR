package team._0mods.ecr.common.capability

import net.minecraft.network.chat.Component

interface PlayerMRU {
    var matrixDestruction: Double

    var matrixType: PlayerMatrixType

    var isInfused: Boolean

    interface PlayerMatrixType {
        val name: Component

        val reduceRadiationMultiplier: Double

        val protectMatrixDecay: Boolean
    }
}