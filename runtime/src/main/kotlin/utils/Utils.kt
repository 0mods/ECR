package com.algorithmlx.ecr.utils

interface PlatformUtils {
    fun isLoaded(modId: String): Boolean

    companion object {
        lateinit var instance: PlatformUtils
    }
}

fun isLoaded(modId: String): Boolean = PlatformUtils.instance.isLoaded(modId)
