package team._0mods.ecr.network

import net.minecraft.server.level.ServerPlayer
import net.minecraftforge.network.NetworkDirection
import net.minecraftforge.network.NetworkRegistry
import net.minecraftforge.network.PacketDistributor
import net.minecraftforge.network.simple.SimpleChannel
import team._0mods.ecr.ModId
import team._0mods.ecr.api.utils.rl
import team._0mods.ecr.network.packets.MithrilineFurnaceS2CUpdatePacket

object ECNetworkManager {
    private const val NETWORK_VER = "1"

    @get:JvmName("getInstance")
    @get:JvmStatic
    lateinit var sc: SimpleChannel

    private var id = 0
        get() = field++

    @JvmStatic
    fun init() {
        sc = NetworkRegistry.newSimpleChannel("$ModId:main".rl, ::NETWORK_VER, NETWORK_VER::equals, NETWORK_VER::equals)

        sc.messageBuilder(MithrilineFurnaceS2CUpdatePacket::class.java, id, NetworkDirection.PLAY_TO_CLIENT)
            .decoder(::MithrilineFurnaceS2CUpdatePacket)
            .encoder(MithrilineFurnaceS2CUpdatePacket::toNetwork)
            .consumerMainThread(MithrilineFurnaceS2CUpdatePacket::handle)
            .add()
    }

    fun <MSG> MSG.sendToServer() {
        sc.sendToServer(this)
    }

    fun <MSG> MSG.sendToPlayer(player: ServerPlayer) {
        sc.send(PacketDistributor.PLAYER.with { player }, this)
    }

    fun <MSG> MSG.sendToClient() {
        sc.send(PacketDistributor.ALL.noArg(), this)
    }
}