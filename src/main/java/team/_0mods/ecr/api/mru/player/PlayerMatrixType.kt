package team._0mods.ecr.api.mru.player

import net.minecraft.network.chat.Component

interface PlayerMatrixType {
    val name: Component

    val reduceRadiationMultiplier: Double

    val protectMatrixDecay: Boolean
}
