package com.algorithmlx.ecr.fabric.init.registry

import com.algorithmlx.ecr.api.utils.ecRL
import com.algorithmlx.ecr.api.registries.ECRegistries
import com.algorithmlx.ecr.api.registries.ECRegistryKeys
import com.algorithmlx.ecr.api.research.BookType
import com.algorithmlx.ecr.common.init.ECRModIDs
import com.algorithmlx.ecr.common.init.registry.BookTypeRegistry
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey

object FabricBookTypeRegistry : BookTypeRegistry {
    private val basicKey = key(ECRModIDs.BASIC)
    private val mruKey = key(ECRModIDs.MRU)
    private val engineerKey = key(ECRModIDs.ENGINEER)
    private val hoannaKey = key(ECRModIDs.HOANNA)
    private val shadeKey = key(ECRModIDs.SHADE)

    override val basic = register(basicKey, simple(0))
    override val mru = register(mruKey, simple(1, setOf(basicKey)))
    override val engineer = register(engineerKey, simple(2, setOf(basicKey, mruKey)))
    override val hoanna = register(hoannaKey, simple(3, setOf(basicKey, mruKey, engineerKey)))
    override val shade = register(shadeKey, simple(4, setOf(basicKey, mruKey, engineerKey, hoannaKey)))

    private fun register(id: ResourceKey<BookType>, level: BookType): BookType =
        Registry.register(ECRegistries.BOOK_TYPES, id, level)

    private fun key(id: String) = ResourceKey.create(ECRegistryKeys.BOOK_TYPE_KEY, id.ecRL)

    private fun simple(levelOrder: Int, inheritedLevels: Set<ResourceKey<BookType>> = setOf()) =
        BookType(levelOrder, inheritedLevels)
}
