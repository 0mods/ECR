package team._0mods.ecr.api.multiblock

import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState

class Multiblock internal constructor(
    private val name: ResourceLocation,
    pattern: Array<Array<String>>,
    private val startChar: Char,
    replaces: Boolean,
    vararg rawMatchers: Any
): IMultiblock {
    private val match: MutableMap<BlockPos, Matcher> = HashMap()
    private val w: Int
    private val h: Int
    private val d: Int
    private val xo: Int
    private val yo: Int
    private val zo: Int
    private val rp: Array<Array<CharArray>>

    init {
        var width = -1
        this.h = pattern.size
        var depth = -1
        var xOff = 0
        var yOff = 0
        var zOff = 0
        var raw: Array<Array<CharArray>> = arrayOf()

        for (i in pattern.indices) {
            val row = pattern[i]

            if (width < 0) width = row.size
            else require(row.size == width)

            for (j in row.indices) {
                val column = row[j]
                if (depth < 0) depth = column.length
                else require(column.length == depth)

                if (raw.isEmpty()) raw = Array(width) { Array(this.h) { CharArray(depth) } }
                for (k in column.indices) {
                    val c = column[k]
                    raw[k][h - 1 - i][j] = c

                    if (c == startChar) {
                        xOff = k
                        yOff = this.h - 1 - i
                        zOff = j
                    }
                }
            }
        }


        this.d = depth
        this.w = width
        this.xo = xOff
        this.yo = yOff
        this.zo = zOff
        this.rp = raw

        val matchers: MutableMap<Char, Matcher> = HashMap()
        var i = 0
        while (i < rawMatchers.size) {
            val c = rawMatchers[i] as Char
            if (matchers.containsKey(c)) {
                i += 2
                continue
            }

            when (val value = rawMatchers[i + 1]) {
                is BlockState -> {
                    matchers[c] = Matcher(
                        value
                    ) { _, _, _, _, other, _ -> other == value }
                }

                is Block -> {
                    matchers[c] = Matcher(
                        value.defaultBlockState()
                    ) { _, _, _, _, state, _ -> state.block == value }
                }

                else -> matchers[c] = value as Matcher
            }
            i += 2
        }

        for (x in 0 ..< this.w) for (y in 0 ..< this.h) for (z in 0 ..< this.d) {
            val matcher = matchers[rp[x][y][z]]
                ?: throw IllegalStateException()
            if (matcher.check != null) this.match[BlockPos(x, y, z)] = matcher
        }

        /*if (!replaces) {
            (ECRegistries.MULTIBLOCKS.registries as LinkedHashMap) += this.name to this
            ECRegistries.MULTIBLOCKS.logReg("Registered: $name")
        } else {
            if (this.name != "nil".ecRL || this.name != "null".ecRL) {
                (ECRegistries.MULTIBLOCKS.registries as LinkedHashMap)[this.name] = this
                ECRegistries.MULTIBLOCKS.logReg("Replaced: $name")
            }
        }*/
    }

    override fun isComplete(level: Level, center: BlockPos): Boolean {
        val start = this.getStart(center)
        return this.forEach(center, startChar) { pos, matcher ->
            val offset = pos.subtract(start)
            matcher.check!!.matches(level, start, offset, pos, level.getBlockState(pos), this.getChar(offset))
        }
    }

    override fun forEach(center: BlockPos, c: Char, function: (BlockPos, Matcher) -> Boolean): Boolean {
        val start = this.getStart(center)
        for ((offset, value) in this.match) {
            if (c.code == 0 || c == startChar || this.getChar(offset) == c) if (!function(start.offset(offset), value)) return false
        }
        return true
    }

    override fun getStart(center: BlockPos): BlockPos {
        return center.offset(-this.xo, -this.yo, -this.zo)
    }

    override fun getChar(offset: BlockPos): Char {
        return rp[offset.x][offset.y][offset.z]
    }

    override fun getName(): ResourceLocation {
        return this.name
    }

    override fun getMatchers(): Map<BlockPos, Matcher> {
        return this.match
    }

    override fun getWidth(): Int {
        return this.w
    }

    override fun getHeight(): Int {
        return this.h
    }

    override fun getDepth(): Int {
        return this.d
    }

    override fun getXOffset(): Int {
        return this.xo
    }

    override fun getYOffset(): Int {
        return this.yo
    }

    override fun getZOffset(): Int {
        return this.zo
    }

    override fun getRawPattern(): Array<Array<CharArray>> {
        return this.rp
    }
}
