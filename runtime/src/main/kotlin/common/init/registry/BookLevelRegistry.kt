package com.algorithmlx.ecr.common.init.registry

import com.algorithmlx.ecr.api.research.BookType

interface BookLevelRegistry {
    val basic: BookType
    val mru: BookType
    val engineer: BookType
    val hoana: BookType
    val shade: BookType

    companion object {
        lateinit var instance: BookLevelRegistry
    }
}
