package team._0mods.ecr.common.init.config

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

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

    val matrixConsuming: Int by SomeDelegate {
        val generation = matrixConfig["generation"]!!
        generation.jsonObject["umbru_get"]!!.jsonPrimitive.int
    }

    val matrixResult: Int by SomeDelegate {
        val generation = matrixConfig["generation"]!!
        generation.jsonObject["mru_exit"]!!.jsonPrimitive.int
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

    private class SomeDelegate<T>(private val g: () -> T): ReadOnlyProperty<Any?, T> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): T {
            val l by lazy(g::invoke)
            return l
        }
    }
}