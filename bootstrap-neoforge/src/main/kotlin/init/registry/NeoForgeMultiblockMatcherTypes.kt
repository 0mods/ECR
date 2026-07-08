package com.algorithmlx.ecr.neoforge.init.registry

import com.algorithmlx.ecr.api.ModId
import com.algorithmlx.ecr.api.init.MultiblockMatcherTypes
import com.algorithmlx.ecr.api.multiblock.*
import com.algorithmlx.ecr.api.registries.ECRegistries
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister

class NeoForgeMultiblockMatcherTypes(bus: IEventBus): MultiblockMatcherTypes {
    private val registry = DeferredRegister.create(ECRegistries.MULTIBLOCK_MATCHER_TYPE, ModId)

    init {
        registry.register(bus)
    }

    private val tagMatcher = registry.register("tag") { _ -> MultiblockMatcherType(TagMultiblockMatcher.CODEC) }
    private val blockMatcher = registry.register("block") { _ -> MultiblockMatcherType(BlockMultiblockMatcher.CODEC) }

    override val tag: MultiblockMatcherType<TagMultiblockMatcher> by lazy { tagMatcher.get() }
    override val block: MultiblockMatcherType<BlockMultiblockMatcher> by lazy { blockMatcher.get() }
}
