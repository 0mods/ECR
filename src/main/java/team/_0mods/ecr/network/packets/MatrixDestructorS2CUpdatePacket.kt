package team._0mods.ecr.network.packets

import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.network.FriendlyByteBuf
import net.minecraftforge.network.NetworkEvent
import team._0mods.ecr.api.network.SimplePacket
import team._0mods.ecr.common.blocks.entity.MatrixDestructorEntity
import team._0mods.ecr.common.container.MithrilineFurnaceContainer
import java.util.function.Supplier

class MatrixDestructorS2CUpdatePacket(val mru: Int, val pos: BlockPos): SimplePacket {
    constructor(buf: FriendlyByteBuf): this(buf.readInt(), buf.readBlockPos())

    override fun toNetwork(buf: FriendlyByteBuf) {
        buf.writeInt(mru)
        buf.writeBlockPos(pos)
    }

    override fun handle(sup: Supplier<NetworkEvent.Context>) {
        sup.get().enqueueWork {
            val level = Minecraft.getInstance().level
            val be = level?.getBlockEntity(pos)

            if (be is MatrixDestructorEntity) {
                val container = Minecraft.getInstance().player?.containerMenu

                be.mruStorage.setMru(mru)

                if (container is MithrilineFurnaceContainer && container.blockEntity?.blockPos == pos) {
                    be.mruStorage.setMru(mru)
                }
            }
        }
    }

}