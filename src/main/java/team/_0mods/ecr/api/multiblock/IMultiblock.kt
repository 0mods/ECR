package team._0mods.ecr.api.multiblock

import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.Level
import team._0mods.ecr.api.rl
import java.util.function.BiFunction

interface IMultiblock {
    companion object {
        @JvmField
        val MULTIBLOCKS: Map<ResourceLocation, IMultiblock> = hashMapOf()

        fun getFromJson(id: String) = getFromJson(id.rl)

        fun getFromJson(id: ResourceLocation): IMultiblock {
            val mb = MULTIBLOCKS[id] ?: throw NullPointerException("Failed to get multiblock from json with id $id")
            return mb
        }

        fun createMultiBlock(id: ResourceLocation, pattern: Array<Array<String>>, vararg rawMatchers: Any) =
            createMultiBlock(id, pattern, '0', *rawMatchers)

        fun createMultiBlock(id: ResourceLocation, pattern: Array<Array<String>>, startChar: Char, vararg rawMatchers: Any) =
            Multiblock(id, pattern, startChar, *rawMatchers)
    }

    fun isComplete(level: Level, center: BlockPos): Boolean

    fun forEach(center: BlockPos, c: Char, function: BiFunction<BlockPos, Matcher, Boolean>): Boolean

    fun getStart(center: BlockPos): BlockPos

    fun getChar(offset: BlockPos): Char

    fun getName(): ResourceLocation

    fun getMatchers(): Map<BlockPos, Matcher>

    fun getWidth(): Int

    fun getHeight(): Int

    fun getDepth(): Int

    fun getXOffset(): Int

    fun getYOffset(): Int

    fun getZOffset(): Int

    fun getRawPattern(): Array<Array<CharArray>>
}