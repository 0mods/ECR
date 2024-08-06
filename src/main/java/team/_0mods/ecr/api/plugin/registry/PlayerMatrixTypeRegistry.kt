package team._0mods.ecr.api.plugin.registry

import net.minecraft.resources.ResourceLocation
import team._0mods.ecr.api.mru.player.PlayerMatrixType

interface PlayerMatrixTypeRegistry {
    val types: Map<ResourceLocation, PlayerMatrixType>

    fun register(type: PlayerMatrixType)
}