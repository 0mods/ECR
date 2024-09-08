package team._0mods.ecr.network

import net.minecraft.network.FriendlyByteBuf
import net.minecraft.server.level.ServerPlayer
import net.minecraftforge.network.NetworkDirection
import net.minecraftforge.network.NetworkRegistry
import net.minecraftforge.network.PacketDistributor
import net.minecraftforge.network.simple.SimpleChannel
import ru.hollowhorizon.hc.client.utils.rl
import team._0mods.ecr.ModId
import team._0mods.ecr.api.network.SimplePacket
import team._0mods.ecr.network.packets.MatrixDestructorS2CUpdatePacket
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

        makeS2C { MithrilineFurnaceS2CUpdatePacket(it) }
        makeS2C { MatrixDestructorS2CUpdatePacket(it) }
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

    private inline fun <reified T: SimplePacket> makeS2C(noinline decoder: (FriendlyByteBuf) -> T) {
        sc.messageBuilder(T::class.java, id, NetworkDirection.PLAY_TO_CLIENT)
            .decoder(decoder)
            .encoder { t, b -> t.toNetwork(b) }
            .consumerMainThread { t, nh -> t.handle(nh) }
            .add()
    }

    private inline fun <reified T: SimplePacket> makeC2S(noinline decoder: (FriendlyByteBuf) -> T) {
        sc.messageBuilder(T::class.java, id, NetworkDirection.PLAY_TO_SERVER)
            .decoder(decoder)
            .encoder { t, b -> t.toNetwork(b) }
            .consumerMainThread { t, nh -> t.handle(nh) }
            .add()
    }
}