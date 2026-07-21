package com.algorithmlx.ecr.registry

import com.algorithmlx.ecr.api.utils.ecRL
import com.algorithmlx.ecr.api.registries.ECRegistries
import com.algorithmlx.ecr.api.registries.ECRegistryKeys
import com.algorithmlx.ecr.api.research.BookType
import com.algorithmlx.ecr.common.init.ECRModIDs
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey

@Suppress("ACTUAL_WITHOUT_EXPECT", "unused")
actual object BookTypeRegistry {
    private val basicKey = key(ECRModIDs.BASIC)
    private val mruKey = key(ECRModIDs.MRU)
    private val engineerKey = key(ECRModIDs.ENGINEER)
    private val hoannaKey = key(ECRModIDs.HOANNA)
    private val shadeKey = key(ECRModIDs.SHADE)

    actual val basic = register(basicKey, simple(0))
    actual val mru = register(mruKey, simple(1, setOf(basicKey)))
    actual val engineer = register(engineerKey, simple(2, setOf(basicKey, mruKey)))
    actual val hoanna = register(hoannaKey, simple(3, setOf(basicKey, mruKey, engineerKey)))
    actual val shade = register(shadeKey, simple(4, setOf(basicKey, mruKey, engineerKey, hoannaKey)))

    private fun register(id: ResourceKey<BookType>, level: BookType): BookType =
        Registry.register(ECRegistries.BOOK_TYPES, id, level)

    private fun key(id: String) = ResourceKey.create(ECRegistryKeys.BOOK_TYPE_KEY, id.ecRL)

    private fun simple(levelOrder: Int, inheritedLevels: Set<ResourceKey<BookType>> = setOf()) =
        BookType(levelOrder, inheritedLevels)
}