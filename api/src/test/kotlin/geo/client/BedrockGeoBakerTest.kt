package com.algorithmlx.ecr.api.geo.client

import com.algorithmlx.ecr.api.geo.file.BedrockBone
import com.algorithmlx.ecr.api.geo.file.BedrockGeometry
import com.algorithmlx.ecr.api.geo.file.GeoVec3
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class BedrockGeoBakerTest {
    @Test
    fun keepsVisibleBoundsOffsetInBedrockBlockUnits() {
        val baked = BedrockGeoBaker.bake(geometry(visibleBoundsOffset = GeoVec3(1.5F, 2.25F, -0.75F)))

        assertEquals(-1.5F, baked.visibleBoundsOffsetX)
        assertEquals(2.25F, baked.visibleBoundsOffsetY)
        assertEquals(-0.75F, baked.visibleBoundsOffsetZ)
    }

    @Test
    fun rejectsDuplicateBoneNames() {
        val error = assertFailsWith<IllegalArgumentException> {
            BedrockGeoBaker.bake(
                geometry(
                    bones = listOf(
                        bone("root"),
                        bone("root")
                    )
                )
            )
        }

        assertTrue(error.message.orEmpty().contains("Duplicate GEO bone"))
    }

    @Test
    fun rejectsMissingBoneParents() {
        val error = assertFailsWith<IllegalArgumentException> {
            BedrockGeoBaker.bake(geometry(bones = listOf(bone("child", "missing"))))
        }

        assertTrue(error.message.orEmpty().contains("missing parent"))
    }

    private fun geometry(
        visibleBoundsOffset: GeoVec3 = GeoVec3.ZERO,
        bones: List<BedrockBone> = listOf(bone("root"))
    ) = BedrockGeometry(
        identifier = "geometry.test",
        textureWidth = 16,
        textureHeight = 16,
        visibleBoundsWidth = 2F,
        visibleBoundsHeight = 2F,
        visibleBoundsOffset = visibleBoundsOffset,
        bones = bones
    )

    private fun bone(name: String, parent: String? = null) = BedrockBone(
        name = name,
        parent = parent,
        pivot = GeoVec3.ZERO,
        rotation = GeoVec3.ZERO,
        mirror = false,
        inflate = 0F,
        cubes = emptyList()
    )
}
