package com.algorithmlx.ecr.common.components

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import io.netty.buffer.ByteBuf
import net.minecraft.core.UUIDUtil
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import java.util.UUID

@JvmRecord
data class SoulStoneComponent(
    val owner: UUID,
    val ownerName: String,
    val capacity: Int
) {
    companion object {
        @JvmField
        val EMPTY = SoulStoneComponent(UUID(0, 0), "", -1)

        val codec: Codec<SoulStoneComponent> = RecordCodecBuilder.create { instance ->
            instance.group(
                UUIDUtil.CODEC.fieldOf("owner").forGetter(SoulStoneComponent::owner),
                Codec.STRING.fieldOf("owner_name").forGetter(SoulStoneComponent::ownerName),
                Codec.INT.fieldOf("capacity").forGetter(SoulStoneComponent::capacity)
            ).apply(instance, ::SoulStoneComponent)
        }

        val codecStream: StreamCodec<ByteBuf, SoulStoneComponent> = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, SoulStoneComponent::owner,
            ByteBufCodecs.STRING_UTF8, SoulStoneComponent::ownerName,
            ByteBufCodecs.INT, SoulStoneComponent::capacity,
            ::SoulStoneComponent
        )
    }
}
