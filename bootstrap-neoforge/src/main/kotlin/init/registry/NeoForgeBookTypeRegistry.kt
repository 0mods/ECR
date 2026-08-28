package com.algorithmlx.ecr.neoforge.init.registry

import com.algorithmlx.ecr.api.ModId
import com.algorithmlx.ecr.api.registries.ECRegistryKeys
import com.algorithmlx.ecr.api.registries.ECRegistries
import com.algorithmlx.ecr.api.research.BookType
import com.algorithmlx.ecr.api.utils.ecRL
import com.algorithmlx.ecr.common.init.ECRModIDs
import com.algorithmlx.ecr.registry.BookTypeRegistry
import net.minecraft.resources.ResourceKey
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister

class NeoForgeBookTypeRegistry(bus: IEventBus): BookTypeRegistry {
    private val bookTypes = DeferredRegister.create(ECRegistries.BOOK_TYPES, ModId)

    init {
        bookTypes.register(bus)
    }

    override val basic: BookType by register(ECRModIDs.BASIC, 0)
    override val mru: BookType by register(ECRModIDs.MRU, 1, ECRModIDs.BASIC)
    override val engineer: BookType by register(ECRModIDs.ENGINEER, 2, ECRModIDs.BASIC, ECRModIDs.MRU)
    override val hoanna: BookType by register(ECRModIDs.HOANNA, 3, ECRModIDs.BASIC, ECRModIDs.MRU, ECRModIDs.ENGINEER)
    override val shade: BookType by register(ECRModIDs.SHADE, 4, ECRModIDs.BASIC, ECRModIDs.MRU, ECRModIDs.ENGINEER, ECRModIDs.HOANNA)

    private fun register(id: String, levelOrder: Int, vararg inheritedTypes: String) = bookTypes.register(id) { _ -> BookType(levelOrder, inheritedTypes.mapTo(linkedSetOf()) { inherited -> ResourceKey.create(ECRegistryKeys.BOOK_TYPE_KEY, inherited.ecRL) }) }
}
