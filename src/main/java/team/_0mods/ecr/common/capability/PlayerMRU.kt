package team._0mods.ecr.common.capability

import kotlinx.serialization.Serializable
import net.minecraft.world.entity.player.Player
import ru.hollowhorizon.hc.common.utils.rl
import ru.hollowhorizon.hc.common.capabilities.CapabilityInstance
import ru.hollowhorizon.hc.common.capabilities.HollowCapabilityV2
import team._0mods.ecr.api.ModId
import team._0mods.ecr.api.mru.PlayerMatrixType
import team._0mods.ecr.api.registries.ECRegistries

@Serializable
@HollowCapabilityV2(Player::class)
class PlayerMRU: CapabilityInstance() {
    var matrixDestruction: Double by syncable(0.0)
    var matrix by syncable("$ModId:basic_matrix")
    var isInfused: Boolean by syncable(false)

    fun getMatrixType(): PlayerMatrixType = ECRegistries.PLAYER_MATRICES.getValue(matrix.rl)

    fun setMatrixType(matrix: PlayerMatrixType): PlayerMRU {
        if (ECRegistries.PLAYER_MATRICES.getKey(matrix) != null)
            this.matrix = ECRegistries.PLAYER_MATRICES.getKey(matrix)!!.toString()

        return this
    }
}