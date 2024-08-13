package team._0mods.ecr.common.init.config

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class ECCommonConfig(
    @SerialName("mithriline_furnace")
    val mithrilineFurnaceConfig: MithrilineFurnace = MithrilineFurnace(),
    @SerialName("matrix_destructor")
    val matrixDestructorConfig: MatrixDestructor = MatrixDestructor()
) {
    companion object {
        @JvmStatic
        lateinit var instance: ECCommonConfig
            internal set
    }

    @Serializable
    data class MithrilineFurnace(
        @SerialName("crystal_positions") val crystalPositions: Array<Offset> = arrayOf(
            Offset(2, 2, 2), Offset(-2, 2, -2),
            Offset(-2, 2, 2), Offset(2, 2, -2),
            Offset(2, 1, 0), Offset(0, 1, 2),
            Offset(-2, 1, 0), Offset(0, 1, -2)
        )
    ) {
        @Serializable
        data class Offset(
            val x: Int,
            val y: Int,
            val z: Int
        )

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as MithrilineFurnace

            return crystalPositions.contentEquals(other.crystalPositions)
        }

        override fun hashCode(): Int {
            return crystalPositions.contentHashCode()
        }
    }

    @Serializable
    data class MatrixDestructor(val ubmruToMruCost: Int = 10)
}