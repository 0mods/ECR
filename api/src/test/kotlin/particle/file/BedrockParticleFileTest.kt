package com.algorithmlx.ecr.api.particle.file

import com.algorithmlx.ecr.api.molang.compiler.eval
import com.algorithmlx.ecr.api.molang.runtime.MolangContext
import com.algorithmlx.ecr.api.molang.runtime.Query
import com.algorithmlx.ecr.api.molang.runtime.VariablesMap
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class BedrockParticleFileTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun decodesSnowstormParticleDefinition() {
        val file = json.decodeFromString<BedrockParticleFile>(PARTICLE_JSON)
        val components = file.particleEffect.components
        val variables = VariablesMap()
        val context = MolangContext(Query.EMPTY, variables)

        components.emitterInitialization?.creationExpression?.eval(context)

        assertEquals("snowstorm:test", file.particleEffect.description.identifier)
        assertEquals(BedrockParticleFile.Material.Cutout, file.particleEffect.description.basicRenderParameters.material)
        assertEquals(0.03f, variables["size"])
        assertEquals(2.2f, variables["lifetime"])
        assertNotNull(components.particleAppearanceBillboard)
        assertNotNull(components.particleAppearanceTinting)
    }

    private companion object {
        val PARTICLE_JSON = """
            {
              "format_version": "1.10.0",
              "particle_effect": {
                "description": {
                  "identifier": "snowstorm:test",
                  "basic_render_parameters": {
                    "material": "particles_alpha",
                    "texture": "ecreimagined:textures/particle/test.png"
                  }
                },
                "components": {
                  "minecraft:emitter_initialization": {
                    "creation_expression": "variable.size = 0.03;variable.lifetime = 2.2;"
                  },
                  "minecraft:particle_lifetime_expression": {
                    "max_lifetime": "variable.lifetime"
                  },
                  "minecraft:particle_appearance_billboard": {
                    "size": ["variable.size", "variable.size"],
                    "facing_camera_mode": "rotate_xyz",
                    "uv": {
                      "texture_width": 16,
                      "texture_height": 16,
                      "uv": [0, 0],
                      "uv_size": [16, 16]
                    }
                  },
                  "minecraft:particle_appearance_tinting": {
                    "color": [1, 0.5, 0.25, 1]
                  }
                }
              }
            }
        """.trimIndent()
    }
}
