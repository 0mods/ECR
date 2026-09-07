package com.algorithmlx.ecr.network

import com.algorithmlx.ecr.api.utils.ecRL
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity
import net.minecraft.world.phys.Vec3

data class MagicShieldPayload(
    val entityId: Int,
    val hitX: Float,
    val hitY: Float,
    val hitZ: Float,
    val blocked: Boolean
): CustomPacketPayload {
    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = TYPE

    companion object {
        @JvmField
        val TYPE = CustomPacketPayload.Type<MagicShieldPayload>("magic_shield".ecRL)

        @JvmField
        val STREAM_CODEC: StreamCodec<FriendlyByteBuf, MagicShieldPayload> = StreamCodec.of(
            { buf, value ->
                buf.writeVarInt(value.entityId)
                buf.writeFloat(value.hitX)
                buf.writeFloat(value.hitY)
                buf.writeFloat(value.hitZ)
                buf.writeBoolean(value.blocked)
            },
            {
                MagicShieldPayload(
                    it.readVarInt(),
                    it.readFloat(), it.readFloat(), it.readFloat(),
                    it.readBoolean()
                )
            }
        )
    }
}

object MagicShieldNetwork {
    var sendToPlayer: (ServerPlayer, MagicShieldPayload) -> Unit = { _, _ -> }

    fun show(level: ServerLevel, targer: Entity, attacker: Entity, blocked: Boolean) {
        val direction = hitDirection(targer, attacker)
        val payload = MagicShieldPayload(
            targer.id, direction.x.toFloat(), direction.y.toFloat(), direction.z.toFloat(), blocked
        )

        level.players().filter { it.distanceToSqr(targer) <= MAX_DIST_SQUARE }.forEach { sendToPlayer(it, payload) }
    }

    private fun hitDirection(target: Entity, attacker: Entity): Vec3 {
        val center = target.boundingBox.center
        var direction = attacker.position().subtract(center)

        if (direction.lengthSqr() < MAX_DIRECTION_LENGTH) {
            direction = target.lookAngle.scale(-1.0)
        }

        return direction.normalize()
    }

    private const val MAX_DIST = 128.0
    private const val MAX_DIST_SQUARE = MAX_DIST * MAX_DIST
    private const val MAX_DIRECTION_LENGTH = 0.000001
}
