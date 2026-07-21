package com.algorithmlx.ecr.registry

import com.algorithmlx.ecr.api.research.BookType

expect object BookTypeRegistry {
    val basic: BookType
    val mru: BookType
    val engineer: BookType
    val hoanna: BookType
    val shade: BookType
}
