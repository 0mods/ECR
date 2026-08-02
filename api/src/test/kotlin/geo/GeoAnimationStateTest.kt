package com.algorithmlx.ecr.api.geo

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GeoAnimationStateTest {
    @Test
    fun mainAnimationIsReplacedAtomically() {
        val state = GeoAnimationState()
        state.play("animation.machine.idle", 1.0)
        state.play("animation.machine.open", 2.0)

        val playback = state.snapshot(3.0).single()
        assertEquals("animation.machine.open", playback.animation)
        assertEquals(2.0, playback.startTimeSeconds)
        assertTrue(state.isPlaying(GeoAnimationState.MAIN_LAYER, "animation.machine.open"))
        assertFalse(state.isPlaying(GeoAnimationState.MAIN_LAYER, "animation.machine.idle"))
    }

    @Test
    fun snapshotAssignsDeferredStartOnlyOnce() {
        val state = GeoAnimationState()
        state.play("animation.machine.idle")

        assertEquals(5.0, state.snapshot(5.0).single().startTimeSeconds)
        assertEquals(5.0, state.snapshot(8.0).single().startTimeSeconds)
    }

    @Test
    fun animationTypesMapToPlaybackDirectionAndLifetime() {
        val state = GeoAnimationState()

        state.play("once", AnimationType.PLAY_ONCE, 1.0)
        assertPlayback(state, "once", GeoLoopMode.ONCE, 1F)

        state.play("freeze", AnimationType.PLAY_FREEZE, 1.0)
        assertPlayback(state, "freeze", GeoLoopMode.HOLD, 1F)

        state.play("looped", AnimationType.PLAY_LOOPED, 1.0)
        assertPlayback(state, "looped", GeoLoopMode.LOOP, 1F)

        state.play("reversed", AnimationType.PLAY_REVERSED, 1.0)
        assertPlayback(state, "reversed", GeoLoopMode.ONCE, -1F)
    }

    @Test
    fun stopRemovesMainLayerImmediately() {
        val state = GeoAnimationState()
        state.play("animation.machine.loop", AnimationType.PLAY_LOOPED, 1.0)

        state.stop(GeoAnimationState.MAIN_LAYER)

        assertFalse(state.isPlaying(GeoAnimationState.MAIN_LAYER))
        assertTrue(state.snapshot(2.0).isEmpty())
    }

    @Test
    fun completedSnapshotCannotRemoveNewPlayback() {
        val state = GeoAnimationState()
        state.play("animation.machine.first", AnimationType.PLAY_ONCE, 1.0)
        val oldPlayback = state.snapshot(1.0).single()
        state.play("animation.machine.second", AnimationType.PLAY_ONCE, 2.0)

        assertFalse(state.removeIfCurrent(oldPlayback))
        assertEquals("animation.machine.second", state.snapshot(2.0).single().animation)
    }

    private fun assertPlayback(
        state: GeoAnimationState,
        animation: String,
        loop: GeoLoopMode,
        speed: Float
    ) {
        val playback = state.snapshot(1.0).single()
        assertEquals(animation, playback.animation)
        assertEquals(loop, playback.loop)
        assertEquals(speed, playback.speed)
    }
}
