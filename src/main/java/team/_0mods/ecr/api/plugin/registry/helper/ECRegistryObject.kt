package team._0mods.ecr.api.plugin.registry.helper

import net.minecraft.resources.ResourceLocation

interface ECRegistryObject<T> {
    val registered: Map<ResourceLocation, T>

    fun <X: T> register(id: String, type: X): () -> X

    fun getValue(id: String): T?

    fun getKey(value: T): ResourceLocation?
}
