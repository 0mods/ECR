@file:Suppress("ACTUAL_WITHOUT_EXPECT")
package com.algorithmlx.ecr.utils

import net.fabricmc.loader.api.FabricLoader

actual fun isLoaded(modId: String): Boolean = FabricLoader.getInstance().isModLoaded(modId)
