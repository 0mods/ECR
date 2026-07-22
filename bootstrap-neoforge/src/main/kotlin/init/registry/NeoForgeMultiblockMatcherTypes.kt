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

    private val tagMatcher = registry.register(ECRModIDs.TAG) { _ -> MultiblockMatcherType(TagMultiblockMatcher.CODEC) }
    private val blockMatcher = registry.register(ECRModIDs.BLOCK) { _ -> MultiblockMatcherType(BlockMultiblockMatcher.CODEC) }
    private val listMatcher = registry.register(ECRModIDs.LIST) { _ ->
        MultiblockMatcherType(ListMultiblockMatcher.CODEC)
    }

    override val tag: MultiblockMatcherType<TagMultiblockMatcher> by lazy { tagMatcher.get() }
    override val block: MultiblockMatcherType<BlockMultiblockMatcher> by lazy { blockMatcher.get() }
    override val list: MultiblockMatcherType<ListMultiblockMatcher> by lazy { listMatcher.get() }
}
