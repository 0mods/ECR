package com.algorithmlx.ecr.api.client

import com.algorithmlx.ecr.api.client.texture.ConnectedTexture
import com.algorithmlx.ecr.api.client.texture.ConnectedTextureMask
import com.algorithmlx.ecr.api.client.texture.ConnectedTextureRegion
import com.algorithmlx.ecr.api.client.texture.ConnectedTextureRotation
import com.algorithmlx.ecr.api.client.texture.ConnectedTextureVariant
import net.minecraft.core.Direction
import net.minecraft.resources.Identifier
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ConnectedTextureMaskTest {
    @Test
    fun mapsVisualSidesToStableWorldDirections() {
        val expected = mapOf(
            Direction.DOWN to listOf(Direction.SOUTH, Direction.EAST, Direction.NORTH, Direction.WEST),
            Direction.UP to listOf(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST),
            Direction.NORTH to listOf(Direction.UP, Direction.WEST, Direction.DOWN, Direction.EAST),
            Direction.SOUTH to listOf(Direction.UP, Direction.EAST, Direction.DOWN, Direction.WEST),
            Direction.WEST to listOf(Direction.UP, Direction.SOUTH, Direction.DOWN, Direction.NORTH),
            Direction.EAST to listOf(Direction.UP, Direction.NORTH, Direction.DOWN, Direction.SOUTH),
        )

        expected.forEach { (face, directions) ->
            assertEquals(directions, (0..3).map { ConnectedTextureMask.direction(face, it) })
        }
    }

    @Test
    fun calculatesVisualSideBits() {
        for (face in Direction.entries) {
            val connected = setOf(
                ConnectedTextureMask.direction(face, 0),
                ConnectedTextureMask.direction(face, 2),
            )

            assertEquals(
                ConnectedTextureMask.TOP or ConnectedTextureMask.BOTTOM,
                ConnectedTextureMask.calculate(face, connected::contains),
            )
        }
    }

    @Test
    fun alwaysResolvesThreeWayConnectionsAsStraightLines() {
        val verticalMasks = listOf(
            ConnectedTextureMask.TOP or ConnectedTextureMask.RIGHT or ConnectedTextureMask.BOTTOM,
            ConnectedTextureMask.TOP or ConnectedTextureMask.BOTTOM or ConnectedTextureMask.LEFT,
        )
        verticalMasks.forEach {
            assertEquals(
                ConnectedTextureMask.TOP or ConnectedTextureMask.BOTTOM,
                ConnectedTextureMask.straightThroughThreeWay(it),
            )
        }

        val horizontalMasks = listOf(
            ConnectedTextureMask.TOP or ConnectedTextureMask.RIGHT or ConnectedTextureMask.LEFT,
            ConnectedTextureMask.RIGHT or ConnectedTextureMask.BOTTOM or ConnectedTextureMask.LEFT,
        )
        horizontalMasks.forEach {
            assertEquals(
                ConnectedTextureMask.LEFT or ConnectedTextureMask.RIGHT,
                ConnectedTextureMask.straightThroughThreeWay(it),
            )
        }
    }

    @Test
    fun resolvesFourWayMaskFromUsableMissingDiagonals() {
        assertEquals(
            ConnectedTextureMask.TOP or ConnectedTextureMask.LEFT,
            ConnectedTextureMask.fourWayFromDiagonals { firstSide, secondSide ->
                firstSide != 3 || secondSide != 0
            },
        )
        assertEquals(
            ConnectedTextureMask.VARIANT_COUNT - 1,
            ConnectedTextureMask.fourWayFromDiagonals { _, _ -> true },
        )
        assertEquals(
            ConnectedTextureMask.LEFT or ConnectedTextureMask.RIGHT,
            ConnectedTextureMask.fourWayFromDiagonals { firstSide, _ ->
                firstSide == 1 || firstSide == 2
            },
        )
        assertEquals(
            ConnectedTextureMask.TOP or ConnectedTextureMask.BOTTOM,
            ConnectedTextureMask.fourWayFromDiagonals { firstSide, _ ->
                firstSide == 2 || firstSide == 3
            },
        )
        assertEquals(
            ConnectedTextureMask.VARIANT_COUNT - 1,
            ConnectedTextureMask.fourWayFromDiagonals { firstSide, _ ->
                firstSide == 1 || firstSide == 3
            },
        )
    }

    @Test
    fun keepsRingConnectedPastSingleBlockBranches() {
        val ring = buildSet {
            for (x in 1..3) {
                add(Cell(x, 1))
                add(Cell(x, 3))
            }
            add(Cell(1, 2))
            add(Cell(3, 2))
        }
        val branches = setOf(
            Cell(2, 0),
            Cell(0, 2),
            Cell(4, 2),
            Cell(2, 4),
        )
        val shape = ring + branches

        assertEquals(
            ConnectedTextureMask.LEFT or ConnectedTextureMask.RIGHT,
            resolvedLineMask(shape, Cell(2, 1)),
        )
        assertEquals(
            ConnectedTextureMask.BOTTOM,
            resolvedLineMask(shape, Cell(2, 0)),
        )
    }

    @Test
    fun tracesSingleRowStaggerWithReciprocalLineEnds() {
        val texture = linePattern()
        val shape = buildSet {
            for (z in 0..1) {
                add(Cell(2, z))
                add(Cell(3, z))
            }
            for (x in 0..3) add(Cell(x, 2))
            for (z in 3..4) {
                add(Cell(0, z))
                add(Cell(1, z))
            }
        }

        assertEquals(
            ConnectedTextureMask.LEFT or ConnectedTextureMask.RIGHT,
            resolvedLineMask(shape, Cell(1, 2)),
        )
        assertEquals(
            ConnectedTextureMask.LEFT or ConnectedTextureMask.RIGHT,
            resolvedLineMask(shape, Cell(2, 2)),
        )
        assertEquals(
            ConnectedTextureMask.TOP,
            resolvedLineMask(shape, Cell(2, 1)),
        )
        assertEquals(
            ConnectedTextureMask.BOTTOM,
            resolvedLineMask(shape, Cell(1, 3)),
        )
        assertTrue(shape.all { texture.variants[resolvedLineMask(shape, it)] != null })
    }

    @Test
    fun keepsThinZigzagConnected() {
        val texture = linePattern()
        val shape = setOf(
            Cell(2, 0),
            Cell(2, 1),
            Cell(1, 1),
            Cell(0, 1),
            Cell(0, 2),
        )

        assertEquals(
            ConnectedTextureMask.LEFT or ConnectedTextureMask.TOP,
            resolvedLineMask(shape, Cell(2, 1)),
        )
        assertEquals(
            ConnectedTextureMask.LEFT or ConnectedTextureMask.RIGHT,
            resolvedLineMask(shape, Cell(1, 1)),
        )
        assertEquals(
            ConnectedTextureMask.RIGHT or ConnectedTextureMask.BOTTOM,
            resolvedLineMask(shape, Cell(0, 1)),
        )
        assertTrue(shape.all { texture.variants[resolvedLineMask(shape, it)] != null })
    }

    @Test
    fun keepsOpenUConnectedWhileIgnoringSingleJunctionLeaf() {
        val shape = setOf(
            Cell(1, 0),
            Cell(0, 1),
            Cell(1, 1),
            Cell(2, 1),
            Cell(0, 2),
            Cell(2, 2),
        )

        assertEquals(
            ConnectedTextureMask.RIGHT or ConnectedTextureMask.BOTTOM,
            resolvedLineMask(shape, Cell(0, 1)),
        )
        assertEquals(
            ConnectedTextureMask.LEFT or ConnectedTextureMask.RIGHT,
            resolvedLineMask(shape, Cell(1, 1)),
        )
        assertEquals(
            ConnectedTextureMask.BOTTOM or ConnectedTextureMask.LEFT,
            resolvedLineMask(shape, Cell(2, 1)),
        )
        assertEquals(
            ConnectedTextureMask.BOTTOM,
            resolvedLineMask(shape, Cell(1, 0)),
        )
    }

    @Test
    fun keepsCrossArmsConnectedUntilDefaultFourWayCenter() {
        val texture = linePattern()
        val center = Cell(0, 0)
        val shape = buildSet {
            add(center)
            for (distance in 1..2) {
                add(Cell(distance, 0))
                add(Cell(-distance, 0))
                add(Cell(0, distance))
                add(Cell(0, -distance))
            }
        }

        assertNull(texture.variants[resolvedLineMask(shape, center)])
        assertEquals(
            ConnectedTextureMask.LEFT or ConnectedTextureMask.RIGHT,
            resolvedLineMask(shape, Cell(1, 0)),
        )
        assertEquals(
            ConnectedTextureMask.TOP or ConnectedTextureMask.BOTTOM,
            resolvedLineMask(shape, Cell(0, -1)),
        )
    }

    @Test
    fun doesNotRouteAroundFilledDiagonalBesideFourWayCenter() {
        val texture = linePattern()
        val center = Cell(0, 0)
        val diagonal = Cell(-1, -1)
        val shape = buildSet {
            add(center)
            add(diagonal)
            for (distance in 1..2) {
                add(Cell(distance, 0))
                add(Cell(-distance, 0))
                add(Cell(0, distance))
                add(Cell(0, -distance))
            }
        }

        assertNull(texture.variants[resolvedLineMask(shape, center)])
        assertEquals(0, resolvedLineMask(shape, diagonal))
        assertNull(texture.variants[resolvedLineMask(shape, diagonal)])
    }

    @Test
    fun rendersInnerCornerTowardsSingleMissingDiagonal() {
        val center = Cell(0, 0)
        val shape = setOf(
            center,
            Cell(0, -1),
            Cell(1, 0),
            Cell(0, 1),
            Cell(-1, 0),
            Cell(1, -1),
            Cell(1, 1),
            Cell(-1, 1),
        )

        assertEquals(
            ConnectedTextureMask.TOP or ConnectedTextureMask.LEFT,
            resolvedLineMask(shape, center),
        )
    }

    @Test
    fun outlinesFilledRectanglePastSingleSideArms() {
        val texture = linePattern()
        val rectangle = filledRectangle(3, 3)
        val arms = setOf(
            Cell(1, -1),
            Cell(3, 1),
            Cell(1, 3),
            Cell(-1, 1),
        )
        val shape = rectangle + arms

        assertEquals(
            ConnectedTextureMask.LEFT or ConnectedTextureMask.RIGHT,
            resolvedLineMask(shape, Cell(1, 0)),
        )
        assertEquals(
            ConnectedTextureMask.TOP or ConnectedTextureMask.BOTTOM,
            resolvedLineMask(shape, Cell(2, 1)),
        )
        assertNull(texture.variants[resolvedLineMask(shape, Cell(1, 1))])
        assertTrue(
            rectangle
                .filterNot { it == Cell(1, 1) }
                .all { texture.variants[resolvedLineMask(shape, it)] != null },
        )
    }

    @Test
    fun packsIndependentMaskForEveryFace() {
        val packed = ConnectedTextureMask.pack { face ->
            (face.get3DDataValue() * 3) and 0xF
        }

        for (face in Direction.entries) {
            assertEquals(
                (face.get3DDataValue() * 3) and 0xF,
                ConnectedTextureMask.unpack(packed, face),
            )
        }
    }

    @Test
    fun createsNumberedVariantIdentifiers() {
        val source = Identifier.parse("example:block/panel")
        val texture = ConnectedTexture.numbered(
            source = source,
            prefix = Identifier.parse("example:block/panel_connected"),
        )

        assertEquals(source, texture.source)
        assertEquals(16, texture.variants.size)
        assertEquals(Identifier.parse("example:block/panel_connected_0"), texture.variants.first()?.sprite)
        assertEquals(Identifier.parse("example:block/panel_connected_15"), texture.variants.last()?.sprite)
    }

    @Test
    fun mapsAllConnectionMasksToMapRegions() {
        val source = Identifier.parse("example:block/panel")
        val map = Identifier.parse("example:block/panel_connected")
        val texture = ConnectedTexture.fromMap(source, map, 20)
        val horizontalColumns = mapOf(
            0 to 0,
            ConnectedTextureMask.RIGHT to 1,
            ConnectedTextureMask.LEFT or ConnectedTextureMask.RIGHT to 2,
            ConnectedTextureMask.LEFT to 3,
        )
        val verticalRows = mapOf(
            0 to 0,
            ConnectedTextureMask.BOTTOM to 1,
            ConnectedTextureMask.TOP or ConnectedTextureMask.BOTTOM to 2,
            ConnectedTextureMask.TOP to 3,
        )

        assertEquals(source, texture.source)
        for (mask in 0..<ConnectedTextureMask.VARIANT_COUNT) {
            val column = horizontalColumns.getValue(
                mask and (ConnectedTextureMask.LEFT or ConnectedTextureMask.RIGHT),
            )
            val row = verticalRows.getValue(
                mask and (ConnectedTextureMask.TOP or ConnectedTextureMask.BOTTOM),
            )
            assertEquals(
                ConnectedTextureVariant(
                    sprite = map,
                    region = ConnectedTextureRegion(column * 20, row * 20, 20, 20),
                ),
                texture.variants[mask],
                "Unexpected map region for mask $mask",
            )
        }
    }

    @Test
    fun validatesMapAndRegionSizes() {
        val source = Identifier.parse("example:block/panel")
        val map = Identifier.parse("example:block/panel_connected")

        assertFailsWith<IllegalArgumentException> {
            ConnectedTexture.fromMap(source, map, 0)
        }
        assertFailsWith<IllegalArgumentException> {
            ConnectedTextureRegion(-1, 0, 20, 20)
        }
        assertFailsWith<IllegalArgumentException> {
            ConnectedTextureRegion(0, 0, 0, 20)
        }
    }

    @Test
    fun createsSparseLinePattern() {
        val source = Identifier.parse("example:block/panel")
        val start = Identifier.parse("example:block/panel/start_line")
        val line = Identifier.parse("example:block/panel/line")
        val angle = Identifier.parse("example:block/panel/angle")
        val texture = ConnectedTexture.linePattern(source, start, line, angle)

        assertVariant(texture, ConnectedTextureMask.RIGHT, start, ConnectedTextureRotation.NONE)
        assertVariant(texture, ConnectedTextureMask.BOTTOM, start, ConnectedTextureRotation.CLOCKWISE_90)
        assertVariant(texture, ConnectedTextureMask.LEFT, start, ConnectedTextureRotation.CLOCKWISE_180)
        assertVariant(texture, ConnectedTextureMask.TOP, start, ConnectedTextureRotation.CLOCKWISE_270)
        assertVariant(
            texture,
            ConnectedTextureMask.LEFT or ConnectedTextureMask.RIGHT,
            line,
            ConnectedTextureRotation.NONE,
        )
        assertVariant(
            texture,
            ConnectedTextureMask.TOP or ConnectedTextureMask.BOTTOM,
            line,
            ConnectedTextureRotation.CLOCKWISE_90,
        )
        assertVariant(
            texture,
            ConnectedTextureMask.RIGHT or ConnectedTextureMask.BOTTOM,
            angle,
            ConnectedTextureRotation.NONE,
        )
        assertVariant(
            texture,
            ConnectedTextureMask.BOTTOM or ConnectedTextureMask.LEFT,
            angle,
            ConnectedTextureRotation.CLOCKWISE_90,
        )
        assertVariant(
            texture,
            ConnectedTextureMask.LEFT or ConnectedTextureMask.TOP,
            angle,
            ConnectedTextureRotation.CLOCKWISE_180,
        )
        assertVariant(
            texture,
            ConnectedTextureMask.TOP or ConnectedTextureMask.RIGHT,
            angle,
            ConnectedTextureRotation.CLOCKWISE_270,
        )

        listOf(0, 7, 11, 13, 14, 15).forEach { mask ->
            assertNull(texture.variants[mask], "Mask $mask must use the default texture")
        }
    }

    @Test
    fun usesDedicatedEndLineWhenProvided() {
        val start = Identifier.parse("example:block/panel/start_line")
        val end = Identifier.parse("example:block/panel/end_line")
        val texture = ConnectedTexture.linePattern(
            source = Identifier.parse("example:block/panel"),
            startLine = start,
            line = Identifier.parse("example:block/panel/line"),
            angle = Identifier.parse("example:block/panel/angle"),
            endLine = end,
        )

        assertVariant(texture, ConnectedTextureMask.RIGHT, start, ConnectedTextureRotation.NONE)
        assertVariant(texture, ConnectedTextureMask.BOTTOM, start, ConnectedTextureRotation.CLOCKWISE_90)
        assertVariant(texture, ConnectedTextureMask.LEFT, end, ConnectedTextureRotation.NONE)
        assertVariant(texture, ConnectedTextureMask.TOP, end, ConnectedTextureRotation.CLOCKWISE_90)
    }

    @Test
    fun usesDedicatedAngleVariantsWhenProvided() {
        val angle = Identifier.parse("example:block/panel/angle")
        val rightAngle = Identifier.parse("example:block/panel/right_angle")
        val downAngle = Identifier.parse("example:block/panel/down_angle")
        val downRightAngle = Identifier.parse("example:block/panel/down_right_angle")
        val texture = ConnectedTexture.linePattern(
            source = Identifier.parse("example:block/panel"),
            startLine = Identifier.parse("example:block/panel/start_line"),
            line = Identifier.parse("example:block/panel/line"),
            angle = angle,
            rightAngle = rightAngle,
            downAngle = downAngle,
            downRightAngle = downRightAngle,
        )

        assertVariant(
            texture,
            ConnectedTextureMask.RIGHT or ConnectedTextureMask.BOTTOM,
            angle,
            ConnectedTextureRotation.NONE,
        )
        assertVariant(
            texture,
            ConnectedTextureMask.BOTTOM or ConnectedTextureMask.LEFT,
            rightAngle,
            ConnectedTextureRotation.NONE,
        )
        assertVariant(
            texture,
            ConnectedTextureMask.LEFT or ConnectedTextureMask.TOP,
            downRightAngle,
            ConnectedTextureRotation.NONE,
        )
        assertVariant(
            texture,
            ConnectedTextureMask.TOP or ConnectedTextureMask.RIGHT,
            downAngle,
            ConnectedTextureRotation.NONE,
        )
    }

    @Test
    fun keepsConnectedVariantsAroundOpenFrames() {
        val texture = linePattern()
        val frame = buildSet {
            for (x in 0..2) {
                add(Cell(x, 0))
                add(Cell(x, 3))
            }
            for (z in 1..2) {
                add(Cell(0, z))
                add(Cell(2, z))
            }
        }

        assertTrue(frame.all { texture.variants[resolvedLineMask(frame, it)] != null })
    }

    @Test
    fun outlinesFilledThreeByThreeRectangle() {
        val texture = linePattern()
        val rectangle = filledRectangle(3, 3)
        val center = Cell(1, 1)

        assertNull(texture.variants[resolvedLineMask(rectangle, center)])
        assertTrue(
            rectangle
                .filterNot { it == center }
                .all { texture.variants[resolvedLineMask(rectangle, it)] != null },
        )
    }

    @Test
    fun keepsCornerOnPreferredThinPath() {
        val texture = linePattern()
        val endpoint = Cell(0, 0)
        val shape = setOf(
            endpoint,
            Cell(-1, 0),
            Cell(0, -1),
            Cell(1, -1),
            Cell(2, -1),
            Cell(1, -2),
        )

        assertEquals(
            ConnectedTextureMask.LEFT or ConnectedTextureMask.TOP,
            resolvedLineMask(shape, endpoint),
        )
        assertVariant(
            texture,
            resolvedLineMask(shape, endpoint),
            Identifier.parse("example:block/panel/angle"),
            ConnectedTextureRotation.CLOCKWISE_180,
        )
    }

    @Test
    fun rejectsIncompleteVariantSets() {
        assertFailsWith<IllegalArgumentException> {
            ConnectedTexture(
                source = Identifier.parse("example:block/panel"),
                variants = List(15) { Identifier.parse("example:block/panel_$it") },
            )
        }
    }

    private fun assertVariant(
        texture: ConnectedTexture,
        mask: Int,
        sprite: Identifier,
        rotation: ConnectedTextureRotation,
    ) {
        assertEquals(ConnectedTextureVariant(sprite, rotation), texture.variants[mask])
    }

    private fun linePattern(): ConnectedTexture = ConnectedTexture.linePattern(
        source = Identifier.parse("example:block/panel"),
        startLine = Identifier.parse("example:block/panel/start_line"),
        line = Identifier.parse("example:block/panel/line"),
        angle = Identifier.parse("example:block/panel/angle"),
    )

    private fun filledRectangle(width: Int, height: Int): Set<Cell> = buildSet {
        for (x in 0..<width) {
            for (z in 0..<height) {
                add(Cell(x, z))
            }
        }
    }

    private fun resolvedLineMask(shape: Set<Cell>, cell: Cell): Int {
        val face = Direction.UP
        val rawMask = rawLineMask(shape, cell)
        val connections = Integer.bitCount(rawMask)
        if (connections < 2) return rawMask

        val preferredMask = if (connections == 4) {
            ConnectedTextureMask.fourWayFromDiagonals { firstSide, secondSide ->
                cell
                    .relative(ConnectedTextureMask.direction(face, firstSide))
                    .relative(ConnectedTextureMask.direction(face, secondSide)) in shape
            }
        } else {
            ConnectedTextureMask.straightThroughThreeWay(rawMask)
        }
        if (preferredMask == ConnectedTextureMask.VARIANT_COUNT - 1) return preferredMask

        return ConnectedTextureMask.withoutRejectedConnections(preferredMask) { side ->
            val neighbour = cell.relative(ConnectedTextureMask.direction(face, side))
            val neighbourMask = rawLineMask(shape, neighbour)
            if (Integer.bitCount(neighbourMask) != 3) {
                true
            } else {
                val oppositeBit = 1 shl ((side + 2) % 4)
                ConnectedTextureMask.straightThroughThreeWay(neighbourMask) and oppositeBit != 0
            }
        }
    }

    private fun rawLineMask(shape: Set<Cell>, cell: Cell): Int =
        ConnectedTextureMask.calculate(Direction.UP) { cell.relative(it) in shape }

    private data class Cell(val x: Int, val z: Int) {
        fun relative(direction: Direction): Cell = Cell(
            x = x + direction.stepX,
            z = z + direction.stepZ,
        )
    }
}
