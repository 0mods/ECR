package com.algorithmlx.ecr.api.client

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

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

    @Test
    fun `mru line animation approaches target smoothly`() {
        val animation = MRULineAnimation()

        assertEquals(0.25, animation.sample(25, 100, 0F))

        val growing = animation.sample(100, 100, 1F)
        val grownFurther = animation.sample(100, 100, 1F)
        assertTrue(growing > 0.25 && growing < 1.0)
        assertTrue(grownFurther > growing && grownFurther < 1.0)

        val shrinking = animation.sample(0, 100, 1F)
        assertTrue(shrinking > 0.0 && shrinking < grownFurther)
    }

    @Test
    fun `mru line animation is independent of frame rate`() {
        val oneFrame = MRULineAnimation()
        oneFrame.sample(0, 100, 0F)
        val oneFrameResult = oneFrame.sample(100, 100, 2F)

        val twoFrames = MRULineAnimation()
        twoFrames.sample(0, 100, 0F)
        twoFrames.sample(100, 100, 1F)
        val twoFrameResult = twoFrames.sample(100, 100, 1F)

        assertEquals(oneFrameResult, twoFrameResult, 0.0000001)
    }
}
