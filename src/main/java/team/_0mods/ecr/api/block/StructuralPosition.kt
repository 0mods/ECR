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

        fun pos(x: Double, y: Double, z: Double): Builder {
            positions += BlockPos(x, y, z)
            return this
        }

        fun pos(vec: Vec3): Builder {
            positions += BlockPos(vec)
            return this
        }

        @get:JvmName("build")
        val build = StructuralPosition(positions)
    }
}