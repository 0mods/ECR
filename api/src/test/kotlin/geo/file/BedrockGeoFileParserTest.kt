package com.algorithmlx.ecr.api.geo.file

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class BedrockGeoFileParserTest {
    @Test
    fun parsesGeometryIdentifierAndBoneCubes() {
        val root = Json.parseToJsonElement(
            """
            {
              "format_version": "1.12.0",
              "minecraft:geometry": [{
                "description": {
                  "identifier": "geometry.test_machine",
                  "texture_width": 32,
                  "texture_height": 32
                },
                "bones": [{
                  "name": "root",
                  "pivot": [0, 0, 0],
                  "cubes": [{"origin": [-4, 0, -4], "size": [8, 8, 8], "uv": [0, 0]}]
                }]
              }]
            }
            """.trimIndent()
        ).jsonObject

        val geometry = BedrockGeoFileParser.parseGeometry(root).single()
        assertEquals("geometry.test_machine", geometry.identifier)
        assertEquals(32, geometry.textureWidth)
        assertEquals("root", geometry.bones.single().name)
        assertEquals(1, geometry.bones.single().cubes.size)
    }

    @Test
    fun parsesAnimationByExactJsonIdentifier() {
        val root = Json.parseToJsonElement(
            """
            {
              "format_version": "1.8.0",
              "animations": {
                "animation.test_machine.open": {
                  "animation_length": 2.0,
                  "bones": {
                    "door": {
                      "rotation": {
                        "0.0": [0, 0, 0],
                        "2.0": {"pre": [0, 90, 0], "post": [0, 90, 0], "lerp_mode": "catmullrom"}
                      }
                    }
                  }
                }
              }
            }
            """.trimIndent()
        ).jsonObject

        val animations = BedrockGeoFileParser.parseAnimations(root)
        val animation = animations.getValue("animation.test_machine.open")
        assertEquals(2F, animation.lengthSeconds)
        assertEquals(GeoInterpolation.CATMULL_ROM, animation.bones.getValue("door").rotation?.keyframes?.last()?.interpolation)
    }

    @Test
    fun rejectsUnsupportedUvRotation() {
        val root = Json.parseToJsonElement(
            """
            {
              "minecraft:geometry": [{
                "description": {"identifier": "geometry.invalid_uv"},
                "bones": [{
                  "name": "root",
                  "cubes": [{
                    "size": [1, 1, 1],
                    "uv": {"north": {"uv": [0, 0], "uv_rotation": 45}}
                  }]
                }]
              }]
            }
            """.trimIndent()
        ).jsonObject

        assertFailsWith<IllegalArgumentException> {
            BedrockGeoFileParser.parseGeometry(root)
        }
    }
}
