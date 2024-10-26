package team._0mods.ecr.api.registries

import net.minecraft.resources.ResourceLocation
import team._0mods.ecr.api.LOGGER
import team._0mods.ecr.api.item.ResearchBookType
import team._0mods.ecr.api.mru.PlayerMatrixType

object ECRegistries {
    @JvmField val PLAYER_MATRICES = SomeRegistry.createRegistry<PlayerMatrixType>("Player Matrices")
    @JvmField val BOOK_TYPES = SomeRegistry.createRegistry<ResearchBookType>("Book Types")
}

class SomeRegistry<T> private constructor(val registryName: String) {
    companion object {
        fun <T> createRegistry(registryName: String): SomeRegistry<T> = SomeRegistry(registryName)
    }

    val registries: Map<ResourceLocation, T> = linkedMapOf()

    fun getValue(id: ResourceLocation): T = if (isPresent(id)) this.getValueOrNull(id)!! else this.registries.values.toList()[0]

    fun getValueOrNull(id: ResourceLocation): T? = registries[id]

    fun getKey(value: T): ResourceLocation? = registries.filter { it.value == value }.keys.toList().getOrNull(0)

    fun isPresent(id: ResourceLocation): Boolean = registries.keys.any { it.toString() == id.toString() }

    fun logReg(logMess: String) {
        LOGGER.info("[$registryName] $logMess")
    }
}
