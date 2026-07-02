package com.algorithmlx.ecr.api

import com.algorithmlx.ecr.ModId
import net.minecraft.resources.Identifier

val String.rl get() = Identifier.parse(this)
val String.ecRL: Identifier
    get() = "$ModId:$this".rl
