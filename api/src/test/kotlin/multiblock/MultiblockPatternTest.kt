package com.algorithmlx.ecr.api.multiblock

import net.minecraft.world.level.block.state.BlockState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class MultiblockPatternTest {
    private val frame = TestMatcher()
    private val interior = TestMatcher()
    private val center = TestMatcher()

    @Test
    fun keepsStrictPatternAtDeclaredSize() {
        val multiblock = Multiblock(2, 1, 1) {
            pattern(frame, interior)
        }

        assertEquals(2, multiblock.xSize)
        assertEquals(1, multiblock.zSize)
        assertEquals(1, multiblock.ySize)
        assertEquals(1, multiblock.variants.size)
        assertEquals(2, multiblock.variants.single().volume)
        assertSame(frame, multiblock.variants.single()[0, 0, 0])
        assertSame(interior, multiblock.variants.single()[1, 0, 0])
    }

    @Test
    fun generatesRadiusVariantsAndKeepsStrictCenter() {
        val multiblock = Multiblock(9, 9, 9) {
            scalablePattern(2..4) {
                when {
                    isCenter -> center
                    isBoundary -> frame
                    else -> interior
                }
            }
        }

        assertEquals(listOf(9, 7, 5), multiblock.variants.map { it.xSize })
        assertEquals(9, multiblock.xSize)
        assertEquals(9, multiblock.zSize)
        assertEquals(9, multiblock.ySize)

        multiblock.variants.forEach { variant ->
            val outerRadius = variant.xSize / 2
            val innerSize = variant.xSize - 2
            val expectedFrameBlocks = variant.volume - innerSize * innerSize * innerSize

            assertEquals(expectedFrameBlocks, variant.blocks.count { it === frame })
            assertEquals(1, variant.blocks.count { it === center })
            assertSame(center, variant[outerRadius, outerRadius, outerRadius])
        }
    }

    @Test
    fun distinguishesWildcardFromRequiredAirMatcher() {
        val wildcard = TestMatcher(required = false)
        val required = TestMatcher()
        val multiblock = Multiblock(2, 1, 1) {
            pattern(wildcard, required)
        }

        assertEquals(false, multiblock.variants.single()[0, 0, 0].required)
        assertEquals(true, multiblock.variants.single()[1, 0, 0].required)
    }

    @Test
    fun generatesFramedFacesAndEmptyCavity() {
        val outerFrame = TestMatcher()
        val innerFrame = TestMatcher()
        val air = TestMatcher()
        val multiblock = Multiblock(5, 5, 5) {
            scalablePattern(2..2) {
                when {
                    isEdge -> outerFrame
                    isBoundary -> innerFrame
                    else -> air
                }
            }
        }
        val variant = multiblock.variants.single()

        assertEquals(44, variant.blocks.count { it === outerFrame })
        assertEquals(54, variant.blocks.count { it === innerFrame })
        assertEquals(27, variant.blocks.count { it === air })
    }

    private class TestMatcher(
        override val required: Boolean = true
    ): MultiblockMatcher {
        override val type: MultiblockMatcherType<*>
            get() = error("Matcher type is not used by pattern generation tests")

        override fun matches(block: BlockState): Boolean = false

        override fun default(): BlockState =
            error("Default block state is not used by pattern generation tests")
    }
}
