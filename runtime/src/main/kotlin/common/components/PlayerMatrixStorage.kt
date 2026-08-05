package com.algorithmlx.ecr.common.components

import net.minecraft.world.entity.player.Player

/** Loader-independent access to the persistent matrix attached to a player. */
interface PlayerMatrixStorage {
    fun getOrCreate(player: Player): PlayerMatrixComponent

    fun set(player: Player, component: PlayerMatrixComponent)

    companion object {
        @JvmStatic
        lateinit var instance: PlayerMatrixStorage
    }
}

val Player.playerMatrix: PlayerMatrixComponent
    get() = PlayerMatrixStorage.instance.getOrCreate(this)

fun Player.setPlayerMatrix(component: PlayerMatrixComponent) {
    PlayerMatrixStorage.instance.set(this, component)
}

/**
 * Mutates a fresh value and stores it back so attachment implementations can
 * reliably mark the player dirty and synchronize the change.
 */
inline fun Player.updatePlayerMatrix(update: PlayerMatrixComponent.() -> Unit): PlayerMatrixComponent {
    val updated = playerMatrix.copy().apply(update)
    setPlayerMatrix(updated)
    return updated
}
