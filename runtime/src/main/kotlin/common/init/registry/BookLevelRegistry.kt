package com.algorithmlx.ecr.common.init.registry

import com.algorithmlx.ecr.api.research.BookLevel

interface BookLevelRegistry {
    val basic: BookLevel

    companion object {
        lateinit var instance: BookLevelRegistry
    }
}
