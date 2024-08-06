package team._0mods.ecr.api.plugin.registry.impl

import net.minecraft.resources.ResourceLocation
import team._0mods.ecr.api.mru.player.PlayerMatrixType
import team._0mods.ecr.api.plugin.registry.PlayerMatrixTypeRegistry

class PlayerMatrixTypeRegistryImpl(private val modId: String): PlayerMatrixTypeRegistry {
    override val types: Map<ResourceLocation, PlayerMatrixType>
        get() = TODO("Not yet implemented")

    override fun register(type: PlayerMatrixType) {
        TODO("Not yet implemented")
    }
}