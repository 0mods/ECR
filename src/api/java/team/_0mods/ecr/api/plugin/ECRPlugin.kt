package team._0mods.ecr.api.plugin

import team._0mods.ecr.api.plugin.registry.BookTypeRegistry
import team._0mods.ecr.api.plugin.registry.PlayerMatrixTypeRegistry

/**
 * Annotation for marking and automatically registering plugin classes.
 *
 * Classes annotated with `@ECRPlugin` must implement [ECRModPlugin] and have a public constructor.
 * The annotation processor detects these classes, initializes them, and registers them.
 *
 * @property modId The mod identifier associated with this plugin.
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class ECRPlugin(val modId: String)

/**
 * Interface for mod plugins that can register custom types.
 *
 * Implementing classes can override the registry methods to add custom behaviors.
 * These classes must have a public constructor and be annotated with [ECRPlugin].
 */
interface ECRModPlugin {
    /**
     * Registers custom player matrix types.
     *
     * @param reg The registry instance for player matrix types.
     */
    fun onMatrixTypeRegistry(reg: PlayerMatrixTypeRegistry) {}

    /**
     * Registers custom book types.
     *
     * @param reg The registry instance for book types.
     */
    fun onBookTypeRegistry(reg: BookTypeRegistry) {}
}
