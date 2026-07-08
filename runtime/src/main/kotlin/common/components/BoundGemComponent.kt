package com.algorithmlx.ecr.common.components

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import io.netty.buffer.ByteBuf
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.Registries
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.Level

@JvmRecord
data class BoundGemComponent(
    val pos: BlockPos,
    val dimension: ResourceKey<Level>,
    val crossDimension: Boolean
) {
    companion object {
        val codec: Codec<BoundGemComponent> = RecordCodecBuilder.create {
            it.group(
                BlockPos.CODEC.fieldOf("block_pos").forGetter(BoundGemComponent::pos),
                ResourceKey.codec(Registries.DIMENSION).fieldOf("dimension").forGetter(BoundGemComponent::dimension),
                Codec.BOOL.fieldOf("cross_dimension").orElseGet { false }.forGetter(BoundGemComponent::crossDimension)
            ).apply(it, ::BoundGemComponent)
        }

        val streamCodec: StreamCodec<ByteBuf, BoundGemComponent> = StreamCodec.composite(
            BlockPos.STREAM_CODEC, BoundGemComponent::pos,
            ResourceKey.streamCodec(Registries.DIMENSION), BoundGemComponent::dimension,
            ByteBufCodecs.BOOL, BoundGemComponent::crossDimension,
            ::BoundGemComponent
        )
    }
}
