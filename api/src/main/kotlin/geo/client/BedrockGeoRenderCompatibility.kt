package com.algorithmlx.ecr.api.geo.client

import com.algorithmlx.ecr.api.LOGGER
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Optional render-mod hooks for the Bedrock GEO renderer.
 *
 * Shader packs replace Minecraft's standard entity pipelines. The instanced GEO
 * path has a custom vertex format and bone-palette shader, so it cannot be
 * transparently replaced by those programs. Loader integrations can install a
 * detector here and the renderer will use its vanilla-geometry fallback while a
 * shader pack is active.
 */
object BedrockGeoRenderCompatibility {
    @Volatile
    private var shaderPackInUseDetector: () -> Boolean = { false }
    private val loggedDetectorFailure = AtomicBoolean()

    @JvmStatic
    fun installShaderPackInUseDetector(detector: () -> Boolean) {
        shaderPackInUseDetector = detector
        loggedDetectorFailure.set(false)
    }

    internal fun canUseGpuRendering(): Boolean = try {
        !shaderPackInUseDetector()
    } catch (error: Throwable) {
        if (loggedDetectorFailure.compareAndSet(false, true)) {
            LOGGER.error(
                "Unable to query shader-pack state; using the Bedrock GEO compatibility renderer",
                error
            )
        }
        false
    }
}
