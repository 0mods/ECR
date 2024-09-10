package team._0mods.ecr.api.plugin.registry

import net.minecraft.network.chat.Component
import team._0mods.ecr.api.plugin.registry.helper.ECRegistryObject
import team._0mods.ecr.api.mru.PlayerMatrixType

interface PlayerMatrixTypeRegistry: ECRegistryObject<PlayerMatrixType> {
    fun register(id: String, c: () -> PlayerMatrixKonstructor): () -> PlayerMatrixType {
        val construct = c()
        val type = object : PlayerMatrixType {
            override val name: Component get() = construct.name
            override val reduceRadiationMultiplier: Double get() = construct.reduceMultiplier
            override val protectMatrixDecay: Boolean get() = construct.protectDecay
        }

        return register(id, type)
    }

    fun makeConstructor(name: Component, reduceMultiplier: Double, protectDecay: Boolean) = PlayerMatrixKonstructor(name, reduceMultiplier, protectDecay)

    data class PlayerMatrixKonstructor(val name: Component, val reduceMultiplier: Double, val protectDecay: Boolean)
}
