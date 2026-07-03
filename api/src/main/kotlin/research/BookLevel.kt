package com.algorithmlx.ecr.api.research

import com.mojang.serialization.Codec
import net.minecraft.network.chat.Component

interface BookLevel {
    /**
     * Type display name in tooltip & GUI
     * @return [Component] of name
     */
    val translate: Component

    val codec: Codec<BookLevel>
}
