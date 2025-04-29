package team._0mods.ecr.common.data

import kotlinx.serialization.Serializable
import team._0mods.ecr.common.items.SoulStone

@Serializable
data class SoulStoneData(
    val entity: String = "", // can't be empty
    val min: Int = SoulStone.defaultCapacityAdd.first,
    val max: Int = SoulStone.defaultCapacityAdd.last
)
