package team._0mods.ecr.network

import kotlinx.serialization.Serializable
import net.minecraft.client.Minecraft
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.world.entity.player.Player
import ru.hollowhorizon.hc.common.network.HollowPacket
import ru.hollowhorizon.hc.common.network.HollowPacketHandler
import kotlin.random.Random

@Serializable
@HollowPacketHandler(HollowPacketHandler.Direction.TO_CLIENT)
class FinishCraftParticle(val x: Double, val y: Double, val z: Double, val count: Int): HollowPacket {
    override fun handle(player: Player) {
        val level = Minecraft.getInstance().level ?: return
        val rand = Random

        for (i in 0 ..< count) {
            level.addParticle(
                ParticleTypes.POOF,
                x,
                y + rand.nextDouble(0.15, 0.6),
                z,
                rand.nextDouble(-0.06, 0.06),
                rand.nextDouble(-0.0, 0.15),
                rand.nextDouble(-0.06, 0.06)
            )
        }
    }
}
