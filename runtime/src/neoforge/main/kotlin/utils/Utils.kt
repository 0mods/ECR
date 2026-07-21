package com.algorithmlx.ecr.utils

import net.neoforged.fml.ModList

actual fun isLoaded(modId: String): Boolean = ModList.get().isLoaded(modId)
