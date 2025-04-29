package team._0mods.ecr.common.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MagicTableIncreaseData(
    val item: String,
    @SerialName("increase_count") val increaseValue: Double,
    @SerialName("mru_count") val mruCounter: Double = 1.0
)