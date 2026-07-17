package com.algorithmlx.ecr.api.utils

import com.algorithmlx.ecr.api.ModId
import net.minecraft.resources.Identifier

val String.rl get() = Identifier.parse(this)
val String.ecRL: Identifier
    get() = "${ModId}:$this".rl

fun makeIntArray(value: Int = 0) = intArrayOf(value)
