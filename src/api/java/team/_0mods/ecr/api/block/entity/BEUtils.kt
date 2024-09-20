package team._0mods.ecr.api.block.entity

import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.level.block.entity.BlockEntity
import kotlin.math.hypot

fun BlockEntity.updateForNearbyPlayers() {
    val level = this.level ?: return
    val packet = this.updatePacket ?: return

    val players = level.players()
    val pos = this.blockPos

    players.forEach {
        if (it is ServerPlayer) {
            if (hypot(it.x - (pos.x + 0.5), it.z - (pos.z + 0.5)) < 64)
                it.connection.send(packet)
        }
    }
}