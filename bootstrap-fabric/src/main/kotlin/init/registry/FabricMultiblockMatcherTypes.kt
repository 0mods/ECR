package com.algorithmlx.ecr.fabric.init.registry

import com.algorithmlx.ecr.api.ecRL
import com.algorithmlx.ecr.api.init.MultiblockMatcherTypes
import com.algorithmlx.ecr.api.multiblock.*
import com.algorithmlx.ecr.api.registries.ECRegistries
import net.minecraft.core.Registry

object FabricMultiblockMatcherTypes: MultiblockMatcherTypes {
    override val tag: MultiblockMatcherType<TagMultiblockMatcher> = register("tag", MultiblockMatcherType(TagMultiblockMatcher.CODEC))
    override val block: MultiblockMatcherType<BlockMultiblockMatcher> = register("block", MultiblockMatcherType(BlockMultiblockMatcher.CODEC))

    private fun <T: MultiblockMatcherType<*>> register(id: String, menu: T): T =
        Registry.register(ECRegistries.MULTIBLOCK_MATCHER_TYPE, id.ecRL, menu)
}
