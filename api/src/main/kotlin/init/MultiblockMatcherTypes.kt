package com.algorithmlx.ecr.api.init

import com.algorithmlx.ecr.api.multiblock.*

interface MultiblockMatcherTypes {
    val tag: MultiblockMatcherType<TagMultiblockMatcher>
    val block: MultiblockMatcherType<BlockMultiblockMatcher>

    companion object {
        lateinit var instance: MultiblockMatcherTypes
    }
}
