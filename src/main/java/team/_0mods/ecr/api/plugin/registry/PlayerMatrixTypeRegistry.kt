package team._0mods.ecr.api.plugin.registry

import net.minecraft.resources.ResourceLocation
import team._0mods.ecr.api.mru.player.PlayerMatrixType

interface PlayerMatrixTypeRegistry {
    val matrixTypes: Map<ResourceLocation, PlayerMatrixType>

    fun getValue(id: ResourceLocation): PlayerMatrixType?

    fun getKey(matrix: PlayerMatrixType): ResourceLocation?

    fun <T: PlayerMatrixType> register(id: String, type: T): () -> T
}