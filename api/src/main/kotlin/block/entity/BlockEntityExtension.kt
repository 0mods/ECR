package com.algorithmlx.ecr.api.block.entity

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import kotlin.math.hypot

fun BlockEntity.syncForNearby() {
    val level = this.level ?: return
    val packet = this.updatePacket ?: return

    val players = level.players()
    val pos = this.blockPos

    players.filterIsInstance<ServerPlayer>()
        .filter { hypot(it.x - (pos.x + 0.5), it.z - (pos.z + 0.5)) < 64 }
        .forEach { it.connection.send(packet) }
}

interface BlockEntityExtensions<T: BlockEntity> {
    fun onPlace(level: Level, state: BlockState, oldState: BlockState, isMoving: Boolean)

    fun onRemove(level: Level, state: BlockState, oldState: BlockState, isMoving: Boolean)

    fun onPlacedBy(level: Level, state: BlockState, placer: LivingEntity?, stack: ItemStack)

    fun <K: T> onTick(level: Level, pos: BlockPos, state: BlockState, blockEntity: K) {}

    fun <K: T> onClientTick(level: Level, pos: BlockPos, state: BlockState, blockEntity: K) {}
}
