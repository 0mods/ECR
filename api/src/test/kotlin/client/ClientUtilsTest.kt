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

        animation.tick(100, 100)
        val growing = animation.sample(100, 100, 1F)
        animation.tick(100, 100)
        val grownFurther = animation.sample(100, 100, 1F)
        assertTrue(growing > 0.25 && growing < 1.0)
        assertTrue(grownFurther > growing && grownFurther < 1.0)

        animation.tick(0, 100)
        val shrinking = animation.sample(0, 100, 1F)
        assertTrue(shrinking > 0.0 && shrinking < grownFurther)
    }

    @Test
    fun `mru line animation interpolates with partial tick`() {
        val animation = MRULineAnimation()
        animation.sample(0, 100, 0F)
        animation.tick(100, 100)

        val tickStart = animation.sample(100, 100, 0F)
        val halfway = animation.sample(100, 100, 0.5F)
        val tickEnd = animation.sample(100, 100, 1F)

        assertTrue(tickStart < halfway && halfway < tickEnd)
        assertEquals((tickStart + tickEnd) * 0.5, halfway, 0.0000001)
    }
}
