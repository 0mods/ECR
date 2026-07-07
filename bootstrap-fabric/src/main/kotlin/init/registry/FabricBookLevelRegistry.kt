package com.algorithmlx.ecr.fabric.init.registry

import com.algorithmlx.ecr.api.ecRL
import com.algorithmlx.ecr.api.registries.ECRegistries
import com.algorithmlx.ecr.api.registries.ECRegistryKeys
import com.algorithmlx.ecr.api.research.BookType
import com.algorithmlx.ecr.common.init.registry.BookLevelRegistry
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey

object FabricBookLevelRegistry : BookLevelRegistry {
    private val basicKey = key("basic")
    private val mruKey = key("mru")
    private val engineerKey = key("engineer")
    private val hoanaKey = key("hoana")
    private val shadeKey = key("shade")

    override val basic = register(basicKey, simple(0))
    override val mru = register(mruKey, simple(1, setOf(basicKey)))
    override val engineer = register(engineerKey, simple(2, setOf(basicKey, mruKey)))
    override val hoana = register(hoanaKey, simple(3, setOf(basicKey, mruKey, engineerKey)))
    override val shade = register(shadeKey, simple(4, setOf(basicKey, mruKey, engineerKey, hoanaKey)))

    private fun register(id: ResourceKey<BookType>, level: BookType): BookType =
        Registry.register(ECRegistries.BOOK_TYPES, id, level)

    private fun key(id: String) = ResourceKey.create(ECRegistryKeys.BOOK_TYPE_KEY, id.ecRL)

    private fun simple(levelOrder: Int, inheritedLevels: Set<ResourceKey<BookType>> = setOf()) =
        BookType(levelOrder, inheritedLevels)
}
