package team._0mods.ecr.api.plugin.registry

import net.minecraft.network.chat.Component
import team._0mods.ecr.api.plugin.registry.helper.ECRegistryObject
import team._0mods.ecr.common.capability.PlayerMRU

interface PlayerMatrixTypeRegistry: ECRegistryObject<PlayerMRU.PlayerMatrixType> {
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
