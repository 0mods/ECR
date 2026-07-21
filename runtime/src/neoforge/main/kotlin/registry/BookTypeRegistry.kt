package com.algorithmlx.ecr.registry

import com.algorithmlx.ecr.api.ModId
import com.algorithmlx.ecr.api.registries.ECRegistries
import com.algorithmlx.ecr.api.research.BookType
import com.algorithmlx.ecr.common.init.ECRModIDs
import net.minecraft.resources.ResourceKey
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister

@Suppress("ACTUAL_WITHOUT_EXPECT", "unused")
actual object BookTypeRegistry {
    private val bookTypes = DeferredRegister.create(ECRegistries.BOOK_TYPES, ModId)

    fun init(bus: IEventBus) {
        bookTypes.register(bus)
    }

    private val basicType = bookTypes.register(ECRModIDs.BASIC) { _ -> simple(0) }
    private val mruType = bookTypes.register(ECRModIDs.MRU) { _ -> simple(1, setOf(basicType.key)) }
    private val engineerType = bookTypes.register(ECRModIDs.ENGINEER) { _ ->
        simple(2, setOf(basicType.key, mruType.key))
    }
    private val hoannaType = bookTypes.register(ECRModIDs.HOANNA) { _ ->
        simple(3, setOf(basicType.key, mruType.key, engineerType.key))
    }
    private val shadeType = bookTypes.register(ECRModIDs.SHADE) { _ ->
        simple(4, setOf(basicType.key, mruType.key, engineerType.key, hoannaType.key))
    }

    actual val basic: BookType by lazy { basicType.get() }
    actual val mru: BookType by lazy { mruType.get() }
    actual val engineer: BookType by lazy { engineerType.get() }
    actual val hoanna: BookType by lazy { hoannaType.get() }
    actual val shade: BookType by lazy { shadeType.get() }

    private fun simple(levelOrder: Int, inheritedTypes: Set<ResourceKey<BookType>> = setOf()) =
        BookType(levelOrder, inheritedTypes)
}