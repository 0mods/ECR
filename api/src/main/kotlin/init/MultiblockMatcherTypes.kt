package com.algorithmlx.ecr.api.init

import com.algorithmlx.ecr.api.multiblock.*

interface MultiblockMatcherTypes {
    val tag: MultiblockMatcherType<TagMultiblockMatcher>
    val block: MultiblockMatcherType<BlockMultiblockMatcher>
    val list: MultiblockMatcherType<ListMultiblockMatcher>

    companion object {
        lateinit var instance: MultiblockMatcherTypes
    }
}
