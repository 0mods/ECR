package com.algorithmlx.ecr.neoforge.init.registry

import com.algorithmlx.ecr.api.ModId
import com.algorithmlx.ecr.api.init.MultiblockMatcherTypes
import com.algorithmlx.ecr.api.multiblock.*
import com.algorithmlx.ecr.api.registries.ECRegistries
import com.algorithmlx.ecr.common.init.ECRModIDs
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister

class NeoForgeMultiblockMatcherTypes(bus: IEventBus): MultiblockMatcherTypes {
    private val registry = DeferredRegister.create(ECRegistries.MULTIBLOCK_MATCHER_TYPE, ModId)

    init {
        registry.register(bus)
    }

    override val tag: MultiblockMatcherType<TagMultiblockMatcher> by register(ECRModIDs.TAG) { MultiblockMatcherType(TagMultiblockMatcher.CODEC) }
    override val block: MultiblockMatcherType<BlockMultiblockMatcher> by register(ECRModIDs.BLOCK) { MultiblockMatcherType(BlockMultiblockMatcher.CODEC) }
    override val list: MultiblockMatcherType<ListMultiblockMatcher> by register(ECRModIDs.LIST) { MultiblockMatcherType(ListMultiblockMatcher.CODEC) }

    private fun <T: MultiblockMatcher> register(id: String, factory: () -> MultiblockMatcherType<T>) = registry.register(id) { _ -> factory() }
}
