package team._0mods.ecr.api.plugin.registry.helper

import net.minecraft.resources.ResourceLocation
import org.jetbrains.annotations.ApiStatus

/**
 * Represents a registry system for managing and accessing registered entries.
 *
 * This interface defines methods for registering, retrieving, and querying objects
 * within a structured registry, identified by unique resource locations.
 *
 * @param T the type of objects managed by this registry.
 */
interface ECRegistryObject<T> {
    /**
     * Retrieves the map of registered entries.
     *
     * The map contains associations between resource locations (IDs) and their respective registered objects.
     *
     * @return a [Map] of [ResourceLocation] identifiers and their corresponding instances of [T].
     */
    val registered: Map<ResourceLocation, T>

    /**
     * Registers an entry into the registry.
     *
     * This method assigns an ID to the object, automatically generating a full resource location (modid:id).
     *
     * @param id the unique identifier for the entry (without the mod prefix).
     * @param obj the object to be registered.
     * @return a lazy-initialized function that returns the registered instance of [X].
     */
    fun <X: T> register(id: String, obj: X): () -> X

    /**
     * Retrieves a registered entry by its identifier.
     *
     * This method searches for the entry at runtime and returns the corresponding object if present.
     *
     * @param id the unique identifier of the entry.
     * @return the registered instance of [T] if found, otherwise `null`.
     */
    fun getValue(id: String): T?

    /**
     * Retrieves the resource location key associated with a registered value.
     *
     * @param value the registered object whose key is being queried.
     * @return the [ResourceLocation] of the object if registered, otherwise `null`.
     *
     * This method is considered obsolete.
     * Use the equivalent function from `ECRegistries` instead.
     */
    @Deprecated("Useless method. Use analog from team._0mods.ecr.api.registries.Registrar.")
    @ApiStatus.ScheduledForRemoval(inVersion = "1.0")
    fun getKey(value: T): ResourceLocation? = null
}

