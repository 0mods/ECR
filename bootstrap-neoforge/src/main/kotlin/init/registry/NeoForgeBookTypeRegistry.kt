package com.algorithmlx.ecr.neoforge.init.registry

import com.algorithmlx.ecr.api.ModId
import com.algorithmlx.ecr.api.registries.ECRegistries
import com.algorithmlx.ecr.api.research.BookType
import com.algorithmlx.ecr.common.init.ECRModIDs
import com.algorithmlx.ecr.registry.BookTypeRegistry
import net.minecraft.resources.ResourceKey
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister

object NeoForgeBookTypeRegistry : BookTypeRegistry {
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

    override val basic: BookType by lazy { basicType.get() }
    override val mru: BookType by lazy { mruType.get() }
    override val engineer: BookType by lazy { engineerType.get() }
    override val hoanna: BookType by lazy { hoannaType.get() }
    override val shade: BookType by lazy { shadeType.get() }

    private fun simple(levelOrder: Int, inheritedTypes: Set<ResourceKey<BookType>> = setOf()) =
        BookType(levelOrder, inheritedTypes)
}