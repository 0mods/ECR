package com.algorithmlx.ecr.api

import net.minecraft.resources.Identifier

val String.rl get() = Identifier.parse(this)
val String.ecRL: Identifier
    get() = "$ModId:$this".rl
