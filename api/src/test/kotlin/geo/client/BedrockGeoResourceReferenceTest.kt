package com.algorithmlx.ecr.api.geo.client

import com.algorithmlx.ecr.api.geo.GeoModel
import net.minecraft.resources.Identifier
import kotlin.test.Test
import kotlin.test.assertEquals

class BedrockGeoResourceReferenceTest {
    @Test
    fun derivesShortAndFullResourceAliases() {
        val full = Identifier.fromNamespaceAndPath("test", "geo/machines/pylon.geo.json")

        assertEquals(
            setOf(
                full,
                Identifier.fromNamespaceAndPath("test", "machines/pylon")
            ),
            BedrockGeoAssets.geometryResourceAliases(full)
        )
    }

    @Test
    fun geoModelKeepsResourceReferenceDistinctFromBedrockIdentifier() {
        val resource = Identifier.fromNamespaceAndPath("test", "pylon")
        val texture = Identifier.fromNamespaceAndPath("test", "textures/block/pylon.png")
        val model = GeoModel(resource, texture)

        assertEquals(resource, model.geometryResource)
        assertEquals(resource.toString(), model.geometry)
    }
}
