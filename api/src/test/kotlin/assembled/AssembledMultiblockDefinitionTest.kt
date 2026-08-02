package com.algorithmlx.ecr.api.assembled

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.resources.Identifier
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.shapes.Shapes
import com.algorithmlx.ecr.api.registries.ECRegistries
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import kotlin.test.assertSame
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class AssembledMultiblockDefinitionTest {
    private val matcher = AssembledBlockMatcher { _: BlockState -> true }

    @Test
    fun rotatesOffsetsAroundController() {
        val offset = BlockPos(2, 3, -1)

        assertEquals(BlockPos(2, 3, -1), AssembledMultiblockDefinition.rotate(offset, Direction.NORTH))
        assertEquals(BlockPos(1, 3, 2), AssembledMultiblockDefinition.rotate(offset, Direction.EAST))
        assertEquals(BlockPos(-2, 3, 1), AssembledMultiblockDefinition.rotate(offset, Direction.SOUTH))
        assertEquals(BlockPos(-1, 3, -2), AssembledMultiblockDefinition.rotate(offset, Direction.WEST))
    }

    @Test
    fun mapsEveryPartRelativeToController() {
        val definition = definition(
            BlockPos.ZERO,
            BlockPos(1, 0, 0),
            BlockPos(0, 1, -1)
        )
        val controller = BlockPos(10, 20, 30)

        assertEquals(
            listOf(
                BlockPos(10, 20, 30),
                BlockPos(10, 20, 31),
                BlockPos(11, 21, 30)
            ),
            definition.worldPositions(controller, Direction.EAST)
        )
    }

    @Test
    fun rejectsMissingControllerAndDuplicateOffsets() {
        assertFailsWith<IllegalArgumentException> {
            definition(BlockPos(1, 0, 0))
        }
        assertFailsWith<IllegalArgumentException> {
            definition(BlockPos.ZERO, BlockPos.ZERO)
        }
    }

    @Test
    fun safelyRejectsVerticalFacingDuringCalculations() {
        val definition = definition(BlockPos.ZERO, BlockPos(1, 0, 0))

        assertNull(AssembledMultiblockDefinition.rotate(BlockPos.ZERO, Direction.UP))
        assertNull(AssembledMultiblockDefinition.rotateShape(Shapes.block(), Direction.DOWN))
        assertNull(definition.worldPosition(BlockPos.ZERO, Direction.UP, definition.parts.first()))
        assertNull(definition.controllerPosition(BlockPos.ZERO, Direction.DOWN, definition.parts.first()))
        assertTrue(definition.worldPositions(BlockPos.ZERO, Direction.UP).isEmpty())
        assertTrue(definition.controllerCandidates(BlockPos.ZERO, Direction.DOWN).isEmpty())
    }

    @Test
    fun rotatesFormedShapeWithStructureFacing() {
        val shape = Shapes.box(0.0, 0.25, 0.0, 0.25, 0.75, 0.5)

        assertBounds(0.5, 0.25, 0.0, 1.0, 0.75, 0.25, assertNotNull(AssembledMultiblockDefinition.rotateShape(shape, Direction.EAST)))
        assertBounds(0.75, 0.25, 0.5, 1.0, 0.75, 1.0, assertNotNull(AssembledMultiblockDefinition.rotateShape(shape, Direction.SOUTH)))
        assertBounds(0.0, 0.25, 0.75, 0.5, 0.75, 1.0, assertNotNull(AssembledMultiblockDefinition.rotateShape(shape, Direction.WEST)))
    }

    @Test
    fun exposesWholeStructureSelectionFromEveryPart() {
        val definition = definition(BlockPos.ZERO, BlockPos(0, -1, 0))
        val controller = BlockPos(10, 20, 30)

        assertBounds(
            0.0, -1.0, 0.0, 1.0, 1.0, 1.0,
            definition.formedSelectionShapeAt(controller, Direction.NORTH, controller)
        )
        assertBounds(
            0.0, 0.0, 0.0, 1.0, 2.0, 1.0,
            definition.formedSelectionShapeAt(controller, Direction.NORTH, controller.below())
        )
    }

    @Test
    fun slicesUnifiedShapeForCollisionButKeepsItWholeForSelection() {
        val anchor = BlockPos(0, -1, 0)
        val controller = BlockPos(10, 20, 30)
        val definition = assembledMultiblock(Identifier.fromNamespaceAndPath("test", "unified_shape")) {
            controller(matcher)
            part(anchor, matcher)
            formedModelAnchor(anchor)
            formedShape(Shapes.box(0.125, 0.25, 0.125, 0.875, 1.75, 0.875))
        }

        assertBounds(
            0.125, 0.25, 0.125, 0.875, 1.0, 0.875,
            definition.formedShapeAt(controller, Direction.NORTH, controller.below())
        )
        assertBounds(
            0.125, 0.0, 0.125, 0.875, 0.75, 0.875,
            definition.formedShapeAt(controller, Direction.NORTH, controller)
        )
        assertBounds(
            0.125, 0.25, 0.125, 0.875, 1.75, 0.875,
            definition.formedSelectionShapeAt(controller, Direction.NORTH, controller.below())
        )
        assertBounds(
            0.125, -0.75, 0.125, 0.875, 0.75, 0.875,
            definition.formedSelectionShapeAt(controller, Direction.NORTH, controller)
        )
    }

    @Test
    fun resolvesControllerCandidatesFromAnyConfiguredPart() {
        val definition = AssembledMultiblockDefinition(
            Identifier.fromNamespaceAndPath("test", "any_part"),
            listOf(
                AssembledMultiblockPart(BlockPos.ZERO, matcher),
                AssembledMultiblockPart(BlockPos(2, 0, -1), matcher)
            ),
            allowAssemblyFromAnyPart = true
        )
        val selectedPart = BlockPos(11, 20, 32)

        assertEquals(
            BlockPos(10, 20, 30),
            definition.controllerPosition(selectedPart, Direction.EAST, definition.parts[1])
        )
        assertTrue(BlockPos(10, 20, 30) in definition.controllerCandidates(selectedPart, Direction.EAST))
    }

    @Test
    fun controllerOnlyDefinitionKeepsLegacyAssemblyOrigin() {
        val definition = definition(BlockPos.ZERO, BlockPos(1, 0, 0))
        val selected = BlockPos(4, 5, 6)

        assertEquals(listOf(selected), definition.controllerCandidates(selected, Direction.SOUTH))
    }

    @Test
    fun dslDoesNotRegisterDefinitions() {
        val id = Identifier.fromNamespaceAndPath("test", "unregistered_assembled")
        val definition = assembledMultiblock(id) {
            controller(matcher)
        }

        assertSame(id, definition.id)
        assertFalse(ECRegistries.ASSEMBLED_MULTIBLOCK.containsKey(id))
    }

    @Test
    fun exposesPreviewBoundsForNegativeOffsets() {
        val definition = AssembledMultiblockDefinition(
            Identifier.fromNamespaceAndPath("test", "preview_bounds"),
            listOf(
                AssembledMultiblockPart(BlockPos.ZERO, matcher),
                AssembledMultiblockPart(BlockPos(-2, 3, 4), matcher)
            )
        )

        assertEquals(BlockPos(-2, 0, 0), definition.minOffset)
        assertEquals(BlockPos(0, 3, 4), definition.maxOffset)
        assertEquals(3, definition.xSize)
        assertEquals(4, definition.ySize)
        assertEquals(5, definition.zSize)
    }

    @Test
    fun configuresFormedModelAnchorFromStructurePart() {
        val anchor = BlockPos(0, -1, 0)
        val definition = assembledMultiblock(Identifier.fromNamespaceAndPath("test", "anchored")) {
            controller(matcher)
            part(anchor, matcher)
            formedModelAnchor(anchor)
        }

        assertEquals(anchor, definition.formedModelAnchor)
        assertFailsWith<IllegalArgumentException> {
            assembledMultiblock(Identifier.fromNamespaceAndPath("test", "invalid_anchor")) {
                controller(matcher)
                formedModelAnchor(2, 0, 0)
            }
        }
    }

    @Test
    fun assembledDefinitionsUseMinecraftRegistryDirectly() {
        val definition = AssembledMultiblockDefinition(
            Identifier.fromNamespaceAndPath("test", "registered_assembled"),
            listOf(AssembledMultiblockPart(BlockPos.ZERO, matcher))
        )

        net.minecraft.core.Registry.register(ECRegistries.ASSEMBLED_MULTIBLOCK, definition.id, definition)
        ECRegistries.ASSEMBLED_MULTIBLOCK.freeze()

        assertSame(definition, ECRegistries.ASSEMBLED_MULTIBLOCK.getOptional(definition.id).orElse(null))
    }

    private fun assertBounds(
        minX: Double,
        minY: Double,
        minZ: Double,
        maxX: Double,
        maxY: Double,
        maxZ: Double,
        shape: net.minecraft.world.phys.shapes.VoxelShape
    ) {
        val bounds = shape.bounds()
        assertTrue(kotlin.math.abs(bounds.minX - minX) < 1.0E-7)
        assertTrue(kotlin.math.abs(bounds.minY - minY) < 1.0E-7)
        assertTrue(kotlin.math.abs(bounds.minZ - minZ) < 1.0E-7)
        assertTrue(kotlin.math.abs(bounds.maxX - maxX) < 1.0E-7)
        assertTrue(kotlin.math.abs(bounds.maxY - maxY) < 1.0E-7)
        assertTrue(kotlin.math.abs(bounds.maxZ - maxZ) < 1.0E-7)
    }

    private fun definition(vararg offsets: BlockPos) = AssembledMultiblockDefinition(
        Identifier.fromNamespaceAndPath("test", "assembled"),
        offsets.map { offset -> AssembledMultiblockPart(offset, matcher) }
    )
}
