package com.algorithmlx.ecr.fabric.utils

import com.algorithmlx.ecr.utils.PlatformUtils
import net.fabricmc.loader.api.FabricLoader

object FabricPlatformUtils : PlatformUtils {
    override fun isLoaded(modId: String): Boolean = FabricLoader.getInstance().isModLoaded(modId)
}
