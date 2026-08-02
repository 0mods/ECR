package com.algorithmlx.ecr.api.geo.client

import com.algorithmlx.ecr.api.geo.GeoAnimationPlayback
import com.algorithmlx.ecr.api.geo.GeoBlendMode
import com.algorithmlx.ecr.api.geo.GeoLoopMode
import com.algorithmlx.ecr.api.geo.file.BedrockGeoFileParser
import com.algorithmlx.ecr.api.molang.runtime.MolangContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class BedrockGeoAnimatorTest {
    @Test
    fun reversedPlaybackStartsAtLastFrameAndMovesBackward() {
        val playback = GeoAnimationPlayback(
            "main",
            ANIMATION_ID,
            10.0,
            -1F,
            1F,
            GeoLoopMode.ONCE,
            GeoBlendMode.ADDITIVE
        )

        assertEquals(2F, BedrockGeoAnimator.animationTime(animation, playback, MolangContext.EMPTY, 10.0))
        assertEquals(1.5F, BedrockGeoAnimator.animationTime(animation, playback, MolangContext.EMPTY, 10.5))
        assertEquals(0F, BedrockGeoAnimator.animationTime(animation, playback, MolangContext.EMPTY, 12.0))
        assertNull(BedrockGeoAnimator.animationTime(animation, playback, MolangContext.EMPTY, 12.1))
        assertFalse(BedrockGeoAnimator.isFinished(animation, playback, MolangContext.EMPTY, 12.0))
        assertTrue(BedrockGeoAnimator.isFinished(animation, playback, MolangContext.EMPTY, 12.1))

        val frozen = playback.copy(speed = 1F, loop = GeoLoopMode.HOLD)
        assertFalse(BedrockGeoAnimator.isFinished(animation, frozen, MolangContext.EMPTY, 20.0))
    }

    private val animation = BedrockGeoFileParser.parseAnimations(
        Json.parseToJsonElement(
            """
            {
              "animations": {
                "$ANIMATION_ID": {
                  "animation_length": 2.0
                }
              }
            }
            """.trimIndent()
        ).jsonObject
    ).getValue(ANIMATION_ID)

    companion object {
        private const val ANIMATION_ID = "animation.test.reversed"
    }
}
