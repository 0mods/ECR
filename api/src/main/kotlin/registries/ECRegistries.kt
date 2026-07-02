package com.algorithmlx.ecr.api.registries

import com.algorithmlx.ecr.api.LOGGER
import net.minecraft.resources.Identifier

object ECRegistries {

}

/**
 * A generic registry system for managing objects identified by resource locations.
 *
 * This class provides functionality to register, retrieve, and log entries within a structured registry.
 *
 * @param T the type of objects managed by this registry.
 * @property registryName the name of the registry.
 */
class Registrar<T> private constructor(val registryName: String) {
    companion object {
        /**
         * Creates a new registry instance with the specified name.
         *
         * @param registryName the name of the registry.
         * @return a new instance of [Registrar].
         */
        fun <T> createRegistry(registryName: String): Registrar<T> = Registrar(registryName)
    }

    /**
     * The map storing registered objects with their associated resource locations.
     */
    val registries: Map<Identifier, T> = linkedMapOf()

    /**
     * Retrieves a registered value by its ID.
     *
     * If the entry is not present, it returns the first registered value instead.
     *
     * @param id the resource location of the entry.
     * @return the registered object or the first entry if not found.
     */
    fun getValue(id: Identifier): T = if (isPresent(id)) this.getValueOrNull(id)!! else this.registries.values.toList()[0]

    /**
     * Retrieves a registered value by its ID, or `null` if not found.
     *
     * @param id the resource location of the entry.
     * @return the registered object if present, otherwise `null`.
     */
    fun getValueOrNull(id: Identifier): T? = registries[id]

    /**
     * Retrieves the resource location key for a given registered value.
     *
     * @param value the registered object.
     * @return the corresponding [Identifier] if found, otherwise `null`.
     */
    fun getKey(value: T): Identifier? = registries.filter { it.value == value }.keys.toList().getOrNull(0)

    /**
     * Checks if an entry with the given ID is present in the registry.
     *
     * @param id the resource location of the entry.
     * @return `true` if the entry exists, otherwise `false`.
     */
    fun isPresent(id: Identifier): Boolean = registries.keys.any { it.toString() == id.toString() }

    /**
     * Logs a message with the registry name as a prefix.
     *
     * @param logMess the message to be logged.
     */
    fun logReg(logMess: String) {
        LOGGER.info("[$registryName] $logMess")
    }
}
