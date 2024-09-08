package team._0mods.ecr.api.registries

import net.minecraft.resources.ResourceLocation
import team._0mods.ecr.api.item.ECBookType
import team._0mods.ecr.api.multiblock.IMultiblock
import team._0mods.ecr.common.capability.PlayerMRU

object ECRegistries {
    @JvmField val MULTIBLOCKS = SomeRegistry.createRegistry<IMultiblock>("Multiblock Registry")
    @JvmField val PLAYER_MATRICES = SomeRegistry.createRegistry<PlayerMRU.PlayerMatrixType>("Player Matrices")
    @JvmField val BOOK_TYPES = SomeRegistry.createRegistry<ECBookType>("Book Types")
}

class SomeRegistry<T> private constructor(val registryName: String) {
    companion object {
        fun <T> createRegistry(registryName: String): SomeRegistry<T> = SomeRegistry(registryName)
    }

    val registries: Map<ResourceLocation, T> = linkedMapOf()

    fun getValue(id: ResourceLocation): T? = registries[id]

    fun getKey(value: T): ResourceLocation? = registries.filter { it.value == value }.keys.toList().getOrNull(0)
}
