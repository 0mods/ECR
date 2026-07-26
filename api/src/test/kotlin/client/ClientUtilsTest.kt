package com.algorithmlx.ecr.api.client

import kotlin.test.Test
import kotlin.test.assertEquals

class ClientUtilsTest {
    @Test
    fun `mru line width stays inside its bounds`() {
        assertEquals(0, calculateMRULineWidth(0, 100, 124))
        assertEquals(31, calculateMRULineWidth(25, 100, 124))
        assertEquals(124, calculateMRULineWidth(100, 100, 124))
        assertEquals(124, calculateMRULineWidth(250, 100, 124))
    }

    @Test
    fun `mru line width handles invalid bounds`() {
        assertEquals(0, calculateMRULineWidth(50, 0, 124))
        assertEquals(0, calculateMRULineWidth(50, -100, 124))
        assertEquals(0, calculateMRULineWidth(50, 100, 0))
        assertEquals(0, calculateMRULineWidth(-50, 100, 124))
    }
}
