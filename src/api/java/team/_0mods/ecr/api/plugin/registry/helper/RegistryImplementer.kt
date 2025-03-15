package team._0mods.ecr.api.plugin.registry.helper

import net.minecraft.resources.ResourceLocation
import ru.hollowhorizon.hc.common.utils.rl
import team._0mods.ecr.api.LOGGER
import team._0mods.ecr.api.registries.Registrar

/**
 * Implements the [ECRegistryObject] interface to provide a concrete registry implementation.
 *
 * This class facilitates the registration and retrieval of objects within a specific registry,
 * ensuring that each entry is uniquely identified within a given mod's namespace.
 *
 * @param T the type of objects managed by this registry.
 * @property modId the mod identifier associated with this registry.
 * @property registry the underlying registrar that manages registered entries.
 */
open class RegistryImplementer<T>(val modId: String, val registry: Registrar<T>): ECRegistryObject<T> {
    /**
     * Retrieves the map of registered entries associated with this mod's namespace.
     *
     * @return a [Map] of [ResourceLocation] identifiers and their corresponding instances of [T].
     */
    override val registered: Map<ResourceLocation, T>
        get() = registry.registries.filter { it.key.namespace == this.modId }

    /**
     * Registers an entry into the registry, ensuring it has a unique identifier.
     *
     * If an entry with the same ID already exists, the registration is skipped.
     *
     * @param id the unique identifier for the entry (without the mod prefix).
     * @param type the object to be registered.
     * @return a lazy-initialized function that returns the registered instance of [X].
     */
    override fun <X : T> register(id: String, type: X): () -> X {
        val rlId = "$modId:$id".rl

        if (registry.registries.keys.stream().noneMatch { it == rlId }) {
            (registry.registries as LinkedHashMap)[rlId] = type
            registry.logReg("Registered: $rlId")
        } else LOGGER.warn(
            "Oh... Mod: {} trying to register entry with id {}, because entry with this id is already registered! Skipping...",
            modId, id
        )
        return { type }
    }

    /**
     * Retrieves a registered entry by its identifier within this mod's namespace.
     *
     * @param id the unique identifier of the entry.
     * @return the registered instance of [T] if found, otherwise `null`.
     */
    override fun getValue(id: String): T? = registered["$modId:$id".rl]
}
