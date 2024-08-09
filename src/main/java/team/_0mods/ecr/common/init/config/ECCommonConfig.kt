package team._0mods.ecr.common.init.config

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class ECCommonConfig(
    @SerialName("mithriline_furnace")
    val mithrilineFurnaceConfig: MithrilineFurnace = MithrilineFurnace()
) {
    companion object {
        @JvmStatic
        lateinit var instance: ECCommonConfig
            internal set
    }

    @Serializable
    data class MithrilineFurnace(
        @SerialName("pylon_offsets")
        val pylonPositions: PylonPositions = PylonPositions()
    ) {

        @Serializable
        data class PylonPositions(
            @SerialName("first")
            val firstPylonOffset: Offset = Offset(2, 2, 2),
            @SerialName("second")
            val secondPylonOffset: Offset = Offset(-2, 2, -2),
            @SerialName("third")
            val thirdPylonOffset: Offset = Offset(-2, 2, 2),
            @SerialName("fourth")
            val fourthPylonOffset: Offset = Offset(2, 2, -2)
        ) {
            @Serializable
            data class Offset(
                val x: Int,
                val y: Int,
                val z: Int
            )
        }
    }
}