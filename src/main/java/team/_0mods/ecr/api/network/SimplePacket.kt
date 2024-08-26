package team._0mods.ecr.api.network

import net.minecraft.network.FriendlyByteBuf
import net.minecraftforge.network.NetworkEvent
import java.util.function.Supplier

interface SimplePacket {
    fun toNetwork(buf: FriendlyByteBuf)

    fun handle(sup: Supplier<NetworkEvent.Context>)
}
