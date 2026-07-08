package com.algorithmlx.ecr.api.multiblock

import com.algorithmlx.ecr.api.init.MultiblockMatcherTypes
import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.Identifier
import net.minecraft.tags.TagKey
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState

data class TagMultiblockMatcher(
    val tag: TagKey<Block>
): MultiblockMatcher {
    override val type: MultiblockMatcherType<*> = MultiblockMatcherTypes.instance.tag

    override fun matches(block: BlockState): Boolean = block.`is`(tag)

    override fun default(): BlockState = BuiltInRegistries.BLOCK.getTagOrEmpty(tag)
        .firstOrNull()?.value()?.defaultBlockState() ?: Blocks.AIR.defaultBlockState()

    companion object {
        private val tagCodec = Identifier.CODEC.xmap(
            { id -> TagKey.create(Registries.BLOCK, id) },
            { tag -> tag.location }
        )

        @JvmField
        val CODEC: MapCodec<TagMultiblockMatcher> = RecordCodecBuilder.mapCodec {
            it.group(
                tagCodec.fieldOf("tag")
                    .forGetter(TagMultiblockMatcher::tag)
            ).apply(it, ::TagMultiblockMatcher)
        }
    }
}

data class BlockMultiblockMatcher(
    val state: BlockState,
    val ignoreTag: Boolean = false
): MultiblockMatcher {
    override val type: MultiblockMatcherType<*> = MultiblockMatcherTypes.instance.block

    override fun matches(block: BlockState): Boolean =
        if (ignoreTag) block.`is`(state.block)
        else block == state

    override fun default(): BlockState = state

    companion object {
        @JvmField
        val CODEC: MapCodec<BlockMultiblockMatcher> = RecordCodecBuilder.mapCodec {
            it.group(
                BlockState.CODEC.fieldOf("state").forGetter(BlockMultiblockMatcher::state),
                Codec.BOOL.optionalFieldOf("ignore_tag", false)
                    .forGetter(BlockMultiblockMatcher::ignoreTag)
            ).apply(it, ::BlockMultiblockMatcher)
        }
    }
}
