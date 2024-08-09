package team._0mods.ecr.api.plugin.registry

import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import team._0mods.ecr.api.plugin.registry.impl.InternalPlayerMatrixTypeRegistry
import team._0mods.ecr.common.capability.PlayerMRU

interface PlayerMatrixTypeRegistry {
    companion object {
        @JvmStatic
        @JvmName("getValue")
        fun getValue(id: ResourceLocation) = InternalPlayerMatrixTypeRegistry.registeredMatrixTypes[id]

        @JvmStatic
        @JvmName("getKey")
        fun getKey(matrix: PlayerMRU.PlayerMatrixType) =
            InternalPlayerMatrixTypeRegistry.registeredMatrixTypes.filter { it.value == matrix }.keys.toList().getOrNull(0)
    }

    val matrixTypes: Map<ResourceLocation, PlayerMRU.PlayerMatrixType>

    fun <T: PlayerMRU.PlayerMatrixType> register(id: String, type: T): () -> T

    fun register(id: String, c: () -> PlayerMatrixKonstructor): () -> PlayerMRU.PlayerMatrixType {
        val construct = c()
        val type = object : PlayerMRU.PlayerMatrixType {
            override val name: Component get() = construct.name
            override val reduceRadiationMultiplier: Double get() = construct.reduceMultiplier
            override val protectMatrixDecay: Boolean get() = construct.protectDecay
        }

        return register(id, type)
    }

    fun makeConstructor(name: Component, reduceMultiplier: Double, protectDecay: Boolean) = PlayerMatrixKonstructor(name, reduceMultiplier, protectDecay)

    data class PlayerMatrixKonstructor(val name: Component, val reduceMultiplier: Double, val protectDecay: Boolean)
}