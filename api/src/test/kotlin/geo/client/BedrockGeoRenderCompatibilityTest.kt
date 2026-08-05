package com.algorithmlx.ecr.api.geo.client

import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BedrockGeoRenderCompatibilityTest {
    @AfterTest
    fun resetDetector() {
        BedrockGeoRenderCompatibility.installShaderPackInUseDetector { false }
    }

    @Test
    fun followsLiveShaderPackState() {
        var shadersActive = false
        BedrockGeoRenderCompatibility.installShaderPackInUseDetector { shadersActive }

        assertTrue(BedrockGeoRenderCompatibility.canUseGpuRendering())
        shadersActive = true
        assertFalse(BedrockGeoRenderCompatibility.canUseGpuRendering())
        shadersActive = false
        assertTrue(BedrockGeoRenderCompatibility.canUseGpuRendering())
    }

    @Test
    fun failsClosedWhenDetectorThrows() {
        BedrockGeoRenderCompatibility.installShaderPackInUseDetector {
            error("broken shader integration")
        }

        assertFalse(BedrockGeoRenderCompatibility.canUseGpuRendering())
    }
}
