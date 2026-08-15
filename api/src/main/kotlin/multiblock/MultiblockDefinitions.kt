package com.algorithmlx.ecr.api.multiblock

import com.algorithmlx.ecr.api.assembled.AssembledMultiblockDefinition
import com.algorithmlx.ecr.api.registries.ECRegistries
import net.minecraft.resources.Identifier
import java.util.Collections

/**
 * Resolves the effective multiblock definitions used at runtime.
 *
 * Code registrations remain the fallback. Definitions loaded from data JSON are
 * installed here and always take priority over that fallback.
 */
object MultiblockDefinitions {
    private val requiredJsonMultiblocks = linkedSetOf<Identifier>()
    private val requiredJsonAssembledMultiblocks = linkedSetOf<Identifier>()

    @Volatile
    private var jsonMultiblocks: Map<Identifier, Multiblock> = emptyMap()

    @Volatile
    private var jsonAssembledMultiblocks: Map<Identifier, AssembledMultiblockDefinition> = emptyMap()

    @Volatile
    private var loadedJsonResources: MultiblockJsonResources = MultiblockJsonResources.EMPTY

    /** Registers an ID whose regular multiblock definition must come from data JSON. */
    @JvmStatic
    @Synchronized
    fun registerJsonOnly(id: Identifier) {
        check(id !in requiredJsonAssembledMultiblocks) {
            "Multiblock ID $id is already registered as a JSON-only assembled multiblock"
        }
        check(!ECRegistries.MULTIBLOCK.containsKey(id)) {
            "Multiblock $id already has a code registration"
        }
        requiredJsonMultiblocks += id
    }

    @JvmStatic
    fun registerJsonOnly(id: String) = registerJsonOnly(Identifier.parse(id))

    /** Registers an ID whose assembled multiblock definition must come from data JSON. */
    @JvmStatic
    @Synchronized
    fun registerJsonOnlyAssembled(id: Identifier) {
        check(id !in requiredJsonMultiblocks) {
            "Multiblock ID $id is already registered as a JSON-only regular multiblock"
        }
        check(!ECRegistries.ASSEMBLED_MULTIBLOCK.containsKey(id)) {
            "Assembled multiblock $id already has a code registration"
        }
        requiredJsonAssembledMultiblocks += id
    }

    @JvmStatic
    fun registerJsonOnlyAssembled(id: String) = registerJsonOnlyAssembled(Identifier.parse(id))

    @JvmStatic
    operator fun get(id: Identifier): Multiblock? =
        jsonMultiblocks[id] ?: ECRegistries.MULTIBLOCK.getOptional(id).orElse(null)

    @JvmStatic
    fun assembled(id: Identifier): AssembledMultiblockDefinition? =
        jsonAssembledMultiblocks[id] ?: ECRegistries.ASSEMBLED_MULTIBLOCK.getOptional(id).orElse(null)

    @JvmStatic
    fun id(definition: Multiblock): Identifier? =
        jsonMultiblocks.entries.firstOrNull { (_, value) -> value === definition }?.key
            ?: ECRegistries.MULTIBLOCK.getKey(definition)

    @JvmStatic
    fun id(definition: AssembledMultiblockDefinition): Identifier? =
        jsonAssembledMultiblocks.entries.firstOrNull { (_, value) -> value === definition }?.key
            ?: ECRegistries.ASSEMBLED_MULTIBLOCK.getKey(definition)

    @JvmStatic
    fun all(): Map<Identifier, Multiblock> = effectiveMap(
        ECRegistries.MULTIBLOCK.keySet(),
        jsonMultiblocks,
        ::get
    )

    @JvmStatic
    fun allAssembled(): Map<Identifier, AssembledMultiblockDefinition> = effectiveMap(
        ECRegistries.ASSEMBLED_MULTIBLOCK.keySet(),
        jsonAssembledMultiblocks,
        ::assembled
    )

    @JvmStatic
    fun hasJsonOverride(id: Identifier): Boolean = id in jsonMultiblocks

    @JvmStatic
    fun hasAssembledJsonOverride(id: Identifier): Boolean = id in jsonAssembledMultiblocks

    @JvmStatic
    fun jsonResources(): MultiblockJsonResources = loadedJsonResources

    @Synchronized
    internal fun requiredJsonIds(): Set<Identifier> = requiredJsonMultiblocks.toSet()

    @Synchronized
    internal fun requiredJsonAssembledIds(): Set<Identifier> = requiredJsonAssembledMultiblocks.toSet()

    @Synchronized
    internal fun installJsonDefinitions(
        multiblocks: Map<Identifier, Multiblock>,
        assembledMultiblocks: Map<Identifier, AssembledMultiblockDefinition>,
        resources: MultiblockJsonResources = loadedJsonResources
    ) {
        jsonMultiblocks = Collections.unmodifiableMap(LinkedHashMap(multiblocks))
        jsonAssembledMultiblocks = Collections.unmodifiableMap(LinkedHashMap(assembledMultiblocks))
        loadedJsonResources = resources.immutableCopy()
    }

    private fun <T : Any> effectiveMap(
        codeIds: Set<Identifier>,
        json: Map<Identifier, T>,
        resolver: (Identifier) -> T?
    ): Map<Identifier, T> {
        val result = linkedMapOf<Identifier, T>()
        (codeIds + json.keys).forEach { id -> resolver(id)?.let { result[id] = it } }
        return Collections.unmodifiableMap(result)
    }
}
