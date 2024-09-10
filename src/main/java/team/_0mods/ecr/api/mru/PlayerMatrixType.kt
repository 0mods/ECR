package team._0mods.ecr.api.mru

import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import team._0mods.ecr.api.registries.ECRegistries

interface PlayerMatrixType {
    companion object {
        @JvmStatic
        fun fromId(id: ResourceLocation): PlayerMatrixType? = ECRegistries.PLAYER_MATRICES.getValueOrNull(id)
    }

    val name: Component

    val reduceRadiationMultiplier: Double

    val protectMatrixDecay: Boolean
}