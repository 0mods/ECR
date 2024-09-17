package team._0mods.ecr.api.plugin.registry.helper

import net.minecraft.resources.ResourceLocation

interface ECRegistryObject<T> {
    /**
     * Registered entries map.
     * @return [Map] of [ResourceLocation] (id) and [T]
     */
    val registered: Map<ResourceLocation, T>

    /**
     * It is a registration of entries
     * @param id sets id for object. Plugin automatically generates full id (modid:id).
     * @param obj entry of registry.
     * @return lazy value of [X].
     */
    fun <X: T> register(id: String, obj: X): () -> X

    /**
     * Returns a value from id.
     * Searches in the current runtime.
     * @param id identifier of entry.
     * @return [T] if entry with [id] is present or else `null`.
     */
    fun getValue(id: String): T?

    /**
     * @return [ResourceLocation] if [value] are registered else `null`
     */
    @Deprecated("Useless method. Use analog from ECRegistries.")
    fun getKey(value: T): ResourceLocation?
}
