package team._0mods.ecr.api.block

import net.minecraft.core.BlockPos
import net.minecraft.world.phys.Vec3

class StructuralPosition private constructor(private val positions: List<BlockPos>) {
    companion object {
        @get:JvmStatic
        @get:JvmName("builder")
        val builder = Builder()

        @JvmStatic
        fun of(positions: List<BlockPos>) = StructuralPosition(positions)
    }

    fun get(pos: BlockPos) = this.positions.map(pos::offset).toList()

    class Builder internal constructor() {
        private val positions: MutableList<BlockPos> = arrayListOf()

        fun pos(x: Int, y: Int, z: Int): Builder {
            positions += BlockPos(x, y, z)
            return this
        }

        fun pos(x: Double, y: Double, z: Double): Builder = this.pos(x.toInt(), y.toInt(), z.toInt())

        fun pos(vec: Vec3): Builder = this.pos(vec.x, vec.y, vec.z)

        @get:JvmName("build")
        val build = StructuralPosition(positions)
    }
}