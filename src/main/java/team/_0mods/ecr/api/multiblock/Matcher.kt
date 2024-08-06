package team._0mods.ecr.api.multiblock

import net.minecraft.core.BlockPos
import net.minecraft.tags.TagKey
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState

@JvmRecord
data class Matcher(val defaultState: BlockState, val check: ICheck?) {
    companion object {
        @JvmStatic
        fun any() = Matcher(Blocks.AIR.defaultBlockState(), null)

        fun tag(default: Block, tag: TagKey<Block>) = Matcher(default.defaultBlockState()) { _, _, _, _, st, _ -> st.`is`(tag) }
    }

    fun interface ICheck {
        fun matches(level: Level, start: BlockPos, offset: BlockPos, pos: BlockPos, state: BlockState, c: Char): Boolean
    }
}
