package com.algorithmlx.ecr.registry

import com.algorithmlx.ecr.api.research.BookType

interface BookTypeRegistry {
    val basic: BookType
    val mru: BookType
    val engineer: BookType
    val hoanna: BookType
    val shade: BookType

    companion object {
        lateinit var instance: BookTypeRegistry
    }
}
