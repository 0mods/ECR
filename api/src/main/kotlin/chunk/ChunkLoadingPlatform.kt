package com.algorithmlx.ecr.api.chunk

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.ChunkPos

interface ChunkLoadingPlatform {
    /**
     * Adds [chunk] to the set of chunks forced by [owner].
     *
     * @return true when the ownership/ticket state changed.
     */
    fun add(level: ServerLevel, owner: BlockPos, chunk: ChunkPos): Boolean

    /**
     * Removes [chunk] from the set of chunks forced by [owner].
     *
     * @return true when the ownership/ticket state changed.
     */
    fun remove(level: ServerLevel, owner: BlockPos, chunk: ChunkPos): Boolean

    /** Removes every forced chunk associated with [owner] in [level]. */
    fun removeAll(level: ServerLevel, owner: BlockPos)

    companion object {
        lateinit var instance: ChunkLoadingPlatform
    }
}
