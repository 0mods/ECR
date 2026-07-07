package com.algorithmlx.ecr.neoforge.init.registry

import com.algorithmlx.ecr.api.ModId
import com.algorithmlx.ecr.api.registries.ECRegistries
import com.algorithmlx.ecr.api.research.BookType
import com.algorithmlx.ecr.common.init.registry.BookLevelRegistry
import net.minecraft.resources.ResourceKey
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister

class NeoForgeBookLevelRegistry(bus: IEventBus) : BookLevelRegistry {
    private val bookTypes = DeferredRegister.create(ECRegistries.BOOK_TYPES, ModId)

    private val basicType = bookTypes.register("basic") { _ -> simple(0) }
    private val mruType = bookTypes.register("mru") { _ -> simple(1, setOf(basicType.key)) }
    private val engineerType = bookTypes.register("engineer") { _ -> 
        simple(2, setOf(basicType.key, mruType.key))
    }
    private val hoanaType = bookTypes.register("hoana") { _ ->
        simple(3, setOf(basicType.key, mruType.key, engineerType.key))
    }
    private val shadeType = bookTypes.register("shade") { _ ->
        simple(4, setOf(basicType.key, mruType.key, engineerType.key, hoanaType.key))
    }

    init {
        bookTypes.register(bus)
    }

    override val basic: BookType by lazy { basicType.get() }
    override val mru: BookType by lazy { mruType.get() }
    override val engineer: BookType by lazy { engineerType.get() }
    override val hoana: BookType by lazy { hoanaType.get() }
    override val shade: BookType by lazy { shadeType.get() }

    private fun simple(levelOrder: Int, inheritedTypes: Set<ResourceKey<BookType>> = setOf()) =
        BookType(levelOrder, inheritedTypes)
}
