package com.algorithmlx.ecr.api.recipe.dsl

import net.minecraft.core.HolderLookup
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeHolder
import net.minecraft.world.item.crafting.RecipeMap
import java.util.concurrent.CopyOnWriteArrayList

// i kept fucking it
object RecipeCompiler {
    private val declarations = CopyOnWriteArrayList<RecipeSpec>()

    @JvmStatic
    fun register(namespace: String, block: RecipesScope.() -> Unit) {
        require(namespace.isNotBlank()) { "Recipe namespace cannot be blank" }

        val scope = RecipesScope(namespace).apply(block)
        val incoming = scope.declarations
        val existing = declarations.asSequence()
            .map(RecipeSpec::id)
            .toHashSet()

        val duplicatesInsideBlock = incoming.groupingBy(RecipeSpec::id)
            .eachCount()
            .filterValues { it > 1 }
            .keys

        require(duplicatesInsideBlock.isEmpty()) {
            "Duplicate code recipe ids in one registration block: $duplicatesInsideBlock"
        }

        val duplicates = incoming.map(RecipeSpec::id).filter { it in existing }

        require(duplicates.isEmpty()) { "Code recipe ids were already registered: $duplicates" }

        declarations += incoming
    }

    @JvmStatic
    fun baked(
        registries: HolderLookup.Provider,
    ): List<RecipeHolder<*>> = declarations.map { it.bake(registries) }

    @JvmStatic
    fun merge(vanilla: RecipeMap, registries: HolderLookup.Provider): RecipeMap {
        val merged = LinkedHashMap<ResourceKey<Recipe<*>>, RecipeHolder<*>>()

        baked(registries).forEach { merged[it.id()] = it }
        vanilla.values().forEach { merged[it.id()] = it }

        return RecipeMap.create(
            merged.values
        )
    }

    @JvmStatic
    fun ids(): Set<Identifier> = declarations.mapTo(
        linkedSetOf(),
        RecipeSpec::id
    )
}

fun recipe(namespace: String, block: RecipesScope.() -> Unit) = RecipeCompiler.register(namespace, block)
