package team._0mods.ecr.common.init.config

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*

@Serializable
class ECCommonConfig(
    @SerialName("mithriline_furnace")
    val mithrilineFurnaceConfig: MithrilineFurnace = MithrilineFurnace(),
    @SerialName("matrix_destructor")
    val matrixConfig: JsonObject = buildJsonObject {
        put("_comment", "Sets the conversion value. Default: 10 for input; 1 for output")
        put("generation", buildJsonObject {
            put("umbru_get", 10)
            put("mru_exit", 1)
        })
    }
) {
    companion object {
        @JvmStatic
        lateinit var instance: ECCommonConfig
            internal set
    }

    /**
     * TODO: Алго блять, каждый тик декодировать из конфига данные - ужасно. Переделай)
      */
    val matrixConsuming: Int get() {
        val generation = matrixConfig["generation"]!!
        return generation.jsonObject["umbru_get"]!!.jsonPrimitive.int
    }

    val matrixResult: Int get() {
        val generation = matrixConfig["generation"]!!
        return generation.jsonObject["mru_exit"]!!.jsonPrimitive.int
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
}