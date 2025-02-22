package team._0mods.ecr.common.init.config

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

@Serializable
class ECCommonConfig(
    @SerialName("mithrilineFurnace")
    val mithrilineFurnaceConfig: MithrilineFurnace = MithrilineFurnace(),
    @SerialName("soulStone")
    val soulStoneConfig: JsonObject = buildJsonObject {
        put("_comment", "Soul stones settings")
        put("standardStone", buildJsonObject {
            put("_comment", "Sets the conversion value. Default: 10 for input; 1 for output")
            put("extracting", buildJsonObject {
                put("input", 10)
                put("output", 1)
            })
        })
    }
) {
    companion object {
        @JvmStatic
        lateinit var instance: ECCommonConfig
            internal set
    }

    val soulStoneExtractCount: Int by SomeDelegate {
        val generation = soulStoneConfig["standardStone"]!!.jsonObject["extracting"]!!
        generation.jsonObject["input"]!!.jsonPrimitive.int
    }

    val soulStoneReceiveCount: Int by SomeDelegate {
        val generation = soulStoneConfig["standardStone"]!!.jsonObject["extracting"]!!
        generation.jsonObject["output"]!!.jsonPrimitive.int
    }

    @Serializable
    data class MithrilineFurnace(
        val crystalPositions: Array<Offset> = arrayOf(
            Offset(2, 2, 2), Offset(-2, 2, -2),
            Offset(-2, 2, 2), Offset(2, 2, -2),
            Offset(2, 1, 0), Offset(0, 1, 2),
            Offset(-2, 1, 0), Offset(0, 1, -2)
        ),
        @SerialName("receivingIfCrystalDisabled")
        val receiveESPEWhenCrystalsInUnavailable: Int = 0,
        val generationReductionLevel: Int = 4
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