package com.algorithmlx.ecr.api.geo.client

import com.algorithmlx.ecr.api.geo.GeoBlockRotation
import net.minecraft.core.Direction
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GeoBlockRotationTest {
    @Test
    fun mapsHorizontalFacingLikeTheBlockRenderer() {
        assertEquals(0F, BedrockGeoRenderEngine.horizontalRotationDegrees(Direction.NORTH))
        assertEquals(-90F, BedrockGeoRenderEngine.horizontalRotationDegrees(Direction.EAST))
        assertEquals(180F, BedrockGeoRenderEngine.horizontalRotationDegrees(Direction.SOUTH))
        assertEquals(90F, BedrockGeoRenderEngine.horizontalRotationDegrees(Direction.WEST))
    }

    @Test
    fun exposesEnabledAndOppositePresets() {
        assertFalse(GeoBlockRotation.NONE.enabled)
        assertTrue(GeoBlockRotation.FACING.enabled)
        assertFalse(GeoBlockRotation.FACING.opposite)
        assertTrue(GeoBlockRotation.OPPOSITE_FACING.enabled)
        assertTrue(GeoBlockRotation.OPPOSITE_FACING.opposite)
    }
}
