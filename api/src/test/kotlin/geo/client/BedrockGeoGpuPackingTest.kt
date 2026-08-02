package com.algorithmlx.ecr.api.geo.client

import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.test.Test
import kotlin.test.assertEquals

class BedrockGeoGpuPackingTest {
    @Test
    fun writesOneBoneIndexForEveryBakedVertex() {
        val model = modelWithTwoBones()
        val stride = 36
        val target = ByteBuffer.allocateDirect(BedrockGeoGpuPacking.vertexCount(model) * stride)
            .order(ByteOrder.nativeOrder())

        BedrockGeoGpuPacking.writeVertices(model, target)

        assertEquals(8, BedrockGeoGpuPacking.vertexCount(model))
        assertEquals(8 * stride, target.position())
        assertEquals(0, target.getInt(32))
        assertEquals(0, target.getInt(3 * stride + 32))
        assertEquals(1, target.getInt(4 * stride + 32))
        assertEquals(1, target.getInt(7 * stride + 32))
    }

    @Test
    fun paletteSizeIncludesMetadataAndBothMatricesForEveryBone() {
        val model = modelWithTwoBones()

        assertEquals(15, BedrockGeoGpuPacking.paletteStrideTexels(model))
        assertEquals(720, BedrockGeoGpuPacking.paletteBytes(model, 3))
    }

    private fun modelWithTwoBones(): BakedGeoModel {
        val quad = BakedGeoQuad(
            floatArrayOf(
                0F, 0F, 0F,
                1F, 0F, 0F,
                1F, 1F, 0F,
                0F, 1F, 0F
            ),
            floatArrayOf(0F, 0F, 1F, 0F, 1F, 1F, 0F, 1F),
            0F,
            0F,
            1F
        )
        val root = BakedGeoBone("root", -1, 0F, 0F, 0F, 0F, 0F, 0F, listOf(quad))
        val child = BakedGeoBone("child", 0, 0F, 0F, 0F, 0F, 0F, 0F, listOf(quad))
        return BakedGeoModel("test", 1F, 1F, 0F, 0F, 0F, listOf(root, child), mapOf("root" to 0, "child" to 1))
    }
}
