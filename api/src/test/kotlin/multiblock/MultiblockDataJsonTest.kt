package com.algorithmlx.ecr.api.multiblock

import com.google.gson.JsonParser
import com.algorithmlx.ecr.api.init.MultiblockMatcherTypes
import com.algorithmlx.ecr.api.registries.ECRegistries
import net.minecraft.core.Registry
import net.minecraft.SharedConstants
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.resources.Identifier
import net.minecraft.server.Bootstrap
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.RotatedPillarBlock
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class MultiblockDataJsonTest {
    private val decoder = MultiblockDataReloadListener()

    @Test
    fun flattensFoundationFirstWithXThenZThenYOrder() {
        val multiblock = decoder.decodeMultiblock(
            json(
                """
                {
                  "pattern": [
                    ["AAA", "ABA"],
                    ["BAB", "BBB"]
                  ],
                  "keys": {
                    "A": "minecraft:stone",
                    "B": "minecraft:dirt"
                  }
                }
                """
            )
        )

        assertEquals(3, multiblock.xSize)
        assertEquals(2, multiblock.zSize)
        assertEquals(2, multiblock.ySize)
        assertEquals(
            listOf(
                Blocks.STONE, Blocks.STONE, Blocks.STONE,
                Blocks.STONE, Blocks.DIRT, Blocks.STONE,
                Blocks.DIRT, Blocks.STONE, Blocks.DIRT,
                Blocks.DIRT, Blocks.DIRT, Blocks.DIRT
            ),
            multiblock.blocks.map { matcher -> matcher.default().block }
        )
    }

    @Test
    fun treatsUnmappedSpacesAsOptionalCells() {
        val multiblock = decoder.decodeMultiblock(
            json(
                """
                {
                  "pattern": [["A A"]],
                  "keys": { "A": "minecraft:stone" }
                }
                """
            )
        )

        assertTrue(multiblock.blocks[0].required)
        assertFalse(multiblock.blocks[1].required)
        assertTrue(multiblock.blocks[2].required)
    }

    @Test
    fun convertsAssembledCoordinatesRelativeToController() {
        val definition = decoder.decodeAssembledMultiblock(
            Identifier.parse("test:assembled"),
            json(
                """
                {
                  "pattern": [
                    ["BBB"],
                    ["ACA"]
                  ],
                  "controller": [1, 1, 0],
                  "keys": {
                    "A": "minecraft:stone",
                    "B": "minecraft:dirt",
                    "C": "minecraft:crafting_table"
                  }
                }
                """
            ),
            null
        )

        assertEquals(
            listOf(
                BlockPos(-1, -1, 0),
                BlockPos(0, -1, 0),
                BlockPos(1, -1, 0),
                BlockPos(-1, 0, 0),
                BlockPos.ZERO,
                BlockPos(1, 0, 0)
            ),
            definition.parts.map { part -> part.offset }
        )
        assertEquals(Blocks.CRAFTING_TABLE, definition.parts.single { it.offset == BlockPos.ZERO }
            .matcher.previewState()?.block)
    }

    @Test
    fun rejectsNonRectangularPatterns() {
        assertFailsWith<IllegalArgumentException> {
            decoder.decodeMultiblock(
                json(
                    """
                    {
                      "pattern": [["AAA", "AA"]],
                      "keys": { "A": "minecraft:stone" }
                    }
                    """
                )
            )
        }
    }

    @Test
    fun decodesRegisteredMatcherCodecObjects() {
        val multiblock = decoder.decodeMultiblock(
            json(
                """
                {
                  "pattern": [["A"]],
                  "keys": {
                    "A": {
                      "type": "block",
                      "state": {
                        "Name": "minecraft:oak_log",
                        "Properties": { "axis": "x" }
                      }
                    }
                  }
                }
                """
            )
        )

        val state = multiblock.blocks.single().default()
        assertEquals(Blocks.OAK_LOG, state.block)
        assertEquals(Direction.Axis.X, state.getValue(RotatedPillarBlock.AXIS))
    }

    private fun json(source: String) = JsonParser.parseString(source.trimIndent()).asJsonObject

    companion object {
        init {
            SharedConstants.tryDetectVersion()
            Bootstrap.bootStrap()
            val tag = registerMatcherType("tag", MultiblockMatcherType(TagMultiblockMatcher.CODEC))
            val block = registerMatcherType("block", MultiblockMatcherType(BlockMultiblockMatcher.CODEC))
            val list = registerMatcherType("list", MultiblockMatcherType(ListMultiblockMatcher.CODEC))
            ECRegistries.MULTIBLOCK_MATCHER_TYPE.freeze()
            MultiblockMatcherTypes.instance = object : MultiblockMatcherTypes {
                override val tag = tag
                override val block = block
                override val list = list
            }
        }

        @Suppress("UNCHECKED_CAST")
        private fun <T : MultiblockMatcher> registerMatcherType(
            path: String,
            type: MultiblockMatcherType<T>
        ): MultiblockMatcherType<T> {
            val id = Identifier.fromNamespaceAndPath("escr", path)
            return if (ECRegistries.MULTIBLOCK_MATCHER_TYPE.containsKey(id)) {
                ECRegistries.MULTIBLOCK_MATCHER_TYPE.getValue(id) as MultiblockMatcherType<T>
            } else {
                Registry.register(ECRegistries.MULTIBLOCK_MATCHER_TYPE, id, type)
            }
        }
    }
}
