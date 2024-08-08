package team._0mods.ecr.network.packets

import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.network.FriendlyByteBuf
import net.minecraftforge.network.NetworkEvent
import team._0mods.ecr.common.blocks.entity.MithrilineFurnaceEntity
import team._0mods.ecr.common.container.MithrilineFurnaceContainer
import java.util.function.Supplier

class MithrilineFurnaceMRUContainerS2CPacket(val mru: Int, val pos: BlockPos) {
    constructor(buf: FriendlyByteBuf): this(buf.readInt(), buf.readBlockPos())

    fun toNetwork(buf: FriendlyByteBuf) {
        buf.writeInt(mru)
        buf.writeBlockPos(pos)
    }

    fun handle(sup: Supplier<NetworkEvent.Context>) {
        sup.get().enqueueWork {
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
}
