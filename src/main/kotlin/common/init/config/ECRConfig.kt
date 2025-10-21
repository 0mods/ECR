package team._0mods.ecr.common.init.config

import com.akuleshov7.ktoml.annotations.TomlComments
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import ru.hollowhorizon.hc.common.config.HollowConfig

@Serializable
class ECRConfig: HollowConfig() {
    @SerialName("mithriline_furnace")
    val mithrilineFurnaceConfig = MithrilineFurnace()
    @SerialName("soul_stone")
    @TomlComments("Soul stone settings")
    val soulStoneConfig = SoulStone()
    @SerialName("cold_distiller")
    val coldDistiller = ColdDistiller()

    @Serializable
    data class SoulStone(
        @TomlComments(inline = "Default: 10")
        val input: Int = 10,
        @TomlComments(inline = "Default: 1")
        val output: Int = 1
    )

    @Serializable
    data class MithrilineFurnace(
        @SerialName("crystal_position")
        val crystalPositions: List<Offset> = listOf(
            Offset(2, 2, 2), Offset(-2, 2, -2),
            Offset(-2, 2, 2), Offset(2, 2, -2),
            Offset(2, 1, 0), Offset(0, 1, 2),
            Offset(-2, 1, 0), Offset(0, 1, -2)
        ),
        @TomlComments(
            "In case you have crystals disabled (crystal_position empty),",
            "Then here you specify the number of how many ESPE/t will be generated.",
            inline = "Default: 0"
        )
        @SerialName("crystal_disabled_generation")
        val receiveESPEWhenCrystalsInUnavailable: Int = 0,
        @TomlComments(
            "Number, by how many times ESPE generation will be reduced, when the block is running",
            inline = "Default: 4"
        )
        @SerialName("reduce_generation")
        val generationReductionLevel: Int = 4
    ) {
        @Serializable
        data class Offset(
            val x: Int,
            val y: Int,
            val z: Int
        )
    }

    @Serializable
    data class ColdDistiller(
        @SerialName("find_radius")
        val findRadius: Int = 3
    )
}
