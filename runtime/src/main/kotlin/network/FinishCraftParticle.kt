package com.algorithmlx.ecr.network

import com.algorithmlx.ecr.api.ecRL
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload

data class FinishCraftParticle(
    val x: Double, val y: Double, val z: Double, val count: Int
): CustomPacketPayload {
    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = TYPE

    companion object {
        @JvmField
        val TYPE = CustomPacketPayload.Type<FinishCraftParticle>("finish_craft_particle".ecRL)

        @JvmField
        val STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.DOUBLE, FinishCraftParticle::x,
            ByteBufCodecs.DOUBLE, FinishCraftParticle::y,
            ByteBufCodecs.DOUBLE, FinishCraftParticle::z,
            ByteBufCodecs.INT, FinishCraftParticle::count,
            ::FinishCraftParticle
        )
    }
}