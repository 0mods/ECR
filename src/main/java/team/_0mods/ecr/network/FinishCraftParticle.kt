package team._0mods.ecr.network

import kotlinx.serialization.Serializable
import net.minecraft.world.entity.player.Player
import ru.hollowhorizon.hc.common.network.HollowPacketV2
import ru.hollowhorizon.hc.common.network.HollowPacketV3
import team._0mods.ecr.common.helper.addFinalParticle

@Serializable
@HollowPacketV2(HollowPacketV2.Direction.TO_CLIENT)
class FinishCraftParticle(val x: Double, val y: Double, val z: Double, val count: Int): HollowPacketV3<FinishCraftParticle> {
    override fun handle(player: Player) {
        addFinalParticle(x, y, z, count)
    }
}