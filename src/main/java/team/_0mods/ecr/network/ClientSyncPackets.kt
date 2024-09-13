package team._0mods.ecr.network

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.player.Player
import ru.hollowhorizon.hc.common.network.HollowPacketV2
import ru.hollowhorizon.hc.common.network.HollowPacketV3
import team._0mods.ecr.common.blocks.entity.MatrixDestructorEntity
import team._0mods.ecr.common.blocks.entity.MithrilineFurnaceEntity
import team._0mods.ecr.common.container.MithrilineFurnaceContainer

@Serializable
@HollowPacketV2(toTarget = HollowPacketV2.Direction.TO_CLIENT)
class ClientMatrixDestructorUpdate(val mru: Int, @Contextual val pos: BlockPos): HollowPacketV3<ClientMatrixDestructorUpdate> {
    override fun handle(player: Player) {
        val level = Minecraft.getInstance().level
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

@Serializable
@HollowPacketV2(toTarget = HollowPacketV2.Direction.TO_CLIENT)
class ClientMithrilineFurnaceUpdate(val mru: Int, @Contextual val pos: BlockPos): HollowPacketV3<ClientMithrilineFurnaceUpdate> {
    override fun handle(player: Player) {
        val level = Minecraft.getInstance().level
        val be = level?.getBlockEntity(pos)

        if (be is MithrilineFurnaceEntity) {
            val container = Minecraft.getInstance().player?.containerMenu

            be.mruStorage.setMru(mru)

            if (container is MithrilineFurnaceContainer && container.blockEntity?.blockPos == pos) {
                be.mruStorage.setMru(mru)
            }
        }
    }
}
