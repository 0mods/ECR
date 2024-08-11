package team._0mods.ecr.api.block

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.util.StringRepresentable
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level

interface Multipart<T> where T: StringRepresentable, T: Enum<T> {
    fun canPlaceAllParts(level: Level, pos: BlockPos, dir: Direction, bpCtx: BlockPlaceContext): Boolean {
        val poss = getAllParts(pos, dir)

        poss.forEach {
            if (!level.getBlockState(it).canBeReplaced(bpCtx)) return false
        }

        return true
    }

    fun getBasePos(pos: BlockPos, dir: Direction, part: T): BlockPos

    fun getAllParts(pos: BlockPos, dir: Direction): Array<BlockPos>
}
