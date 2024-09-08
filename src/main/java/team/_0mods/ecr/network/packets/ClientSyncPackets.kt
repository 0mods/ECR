package team._0mods.ecr.network.packets

import kotlinx.serialization.Serializable
import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.player.Player
import ru.hollowhorizon.hc.common.network.HollowPacketV2
import ru.hollowhorizon.hc.common.network.HollowPacketV3
import team._0mods.ecr.common.blocks.entity.MatrixDestructorEntity
import team._0mods.ecr.common.container.MithrilineFurnaceContainer

// TODO("HollowHorizon needs to fix this")
@HollowPacketV2(toTarget = HollowPacketV2.Direction.TO_CLIENT)
@Serializable
class ClientMatrixDestructorUpdate(val mru: Int, val x: Int, val y: Int, val z: Int): HollowPacketV3<ClientMatrixDestructorUpdate> {
    override fun handle(player: Player) {
        val level = Minecraft.getInstance().level
        val pos = BlockPos(x, y, z)
        val be = level?.getBlockEntity(pos)
        if (be != null && be is MatrixDestructorEntity) {
            val container = Minecraft.getInstance().player?.containerMenu

            be.mruStorage.setMru(mru)

            if (container is MithrilineFurnaceContainer && container.blockEntity?.blockPos == pos) {
                be.mruStorage.setMru(mru)
            }
        }
    }
}
