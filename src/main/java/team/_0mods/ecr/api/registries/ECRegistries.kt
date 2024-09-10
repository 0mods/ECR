package team._0mods.ecr.api.registries

import net.minecraft.resources.ResourceLocation
import team._0mods.ecr.LOGGER
import team._0mods.ecr.api.item.ECBookType
import team._0mods.ecr.api.mru.PlayerMatrixType
import team._0mods.ecr.api.multiblock.IMultiblock
import team._0mods.ecr.api.utils.ecRL

object ECRegistries {
    @JvmField val MULTIBLOCKS = SomeRegistry.createRegistry<IMultiblock>("Multiblock Registry", "nil".ecRL)
    @JvmField val PLAYER_MATRICES = SomeRegistry.createRegistry<PlayerMatrixType>("Player Matrices", "basic_matrix".ecRL)
    @JvmField val BOOK_TYPES = SomeRegistry.createRegistry<ECBookType>("Book Types", "basic".ecRL)
}

class SomeRegistry<T> private constructor(val registryName: String, val defaultId: ResourceLocation) {
    companion object {
        fun <T> createRegistry(registryName: String, defaultId: ResourceLocation): SomeRegistry<T> = SomeRegistry(registryName, defaultId)
    }

    val registries: Map<ResourceLocation, T> = linkedMapOf()

    fun getValue(id: ResourceLocation): T = if (isPresent(id)) this.getValueOrNull(id)!! else this.getValueOrNull(defaultId)!!

    fun getValueOrNull(id: ResourceLocation): T? = registries[id]

    fun getKey(value: T): ResourceLocation? = registries.filter { it.value == value }.keys.toList().getOrNull(0)

    fun isPresent(id: ResourceLocation): Boolean = registries.keys.any { it.toString() == id.toString() }

    fun logReg(logMess: String) {
        LOGGER.info("[$registryName] $logMess")
    }
}
