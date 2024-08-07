package team._0mods.ecr.api.plugin.registry

import net.minecraft.resources.ResourceLocation
import team._0mods.ecr.ECCoroutine
import team._0mods.ecr.api.mru.player.PlayerMatrixType
import team._0mods.ecr.common.init.registry.ECCorePlugin

interface PlayerMatrixTypeRegistry {
    companion object {
        @JvmStatic
        @JvmName("getValue")
        fun getValueJVM(id: ResourceLocation) = ECCorePlugin.value(id)

        @JvmStatic
        @JvmName("getKey")
        fun getKeyJVM(matrix: PlayerMatrixType) = ECCorePlugin.key(matrix)
    }

    val matrixTypes: Map<ResourceLocation, PlayerMatrixType>

    fun getValue(id: ResourceLocation): PlayerMatrixType?

    fun getKey(matrix: PlayerMatrixType): ResourceLocation?

    fun <T: PlayerMatrixType> register(id: String, type: T): () -> T
}