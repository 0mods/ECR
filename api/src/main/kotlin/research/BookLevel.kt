package com.algorithmlx.ecr.api.research

import net.minecraft.network.chat.Component

interface BookLevel {
    /**
     * Type display name in tooltip & GUI
     * @return [Component] of name
     */
    val translate: Component
}
