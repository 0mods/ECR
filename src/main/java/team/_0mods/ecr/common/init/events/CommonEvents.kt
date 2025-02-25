package team._0mods.ecr.common.init.events

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import net.minecraft.client.Minecraft
import ru.hollowhorizon.hc.common.events.entity.player.PlayerInteractEvent
import ru.hollowhorizon.hc.common.events.registry.RegisterCommandsEvent
import ru.hollowhorizon.hc.common.events.registry.RegisterReloadListenersEvent
import team._0mods.ecr.client.screen.book.ECBookScreen
import team._0mods.ecr.common.data.ResearchBookData
import team._0mods.ecr.common.init.registry.ECCommands
import team._0mods.ecr.common.init.registry.reload.ConfigReloadListener
import team._0mods.ecr.common.init.registry.reload.MagicTableIncreaseDataReloadListener
import team._0mods.ecr.common.init.registry.reload.SoulStoneDataReloadListener
import team._0mods.ecr.common.items.ECBook
import team._0mods.ecr.common.items.ECBook.Companion.bookTypes
import ru.hollowhorizon.hc.common.events.SubscribeEvent as HCSubscribe

@OptIn(ExperimentalSerializationApi::class)
private val json = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
    prettyPrint = true
    prettyPrintIndent = "  "
    coerceInputValues = true
    allowComments = true
    allowTrailingComma = true
}

@HCSubscribe
fun onBookUsed(e: PlayerInteractEvent.ItemInteract) {
    val player = e.player
    val level = player.level()
    val hand = e.hand
    val stack = player.getItemInHand(hand)
    val item = stack.item

    if (item is ECBook) {
        val type = stack.bookTypes
        if (level.isClientSide) {
            if (player.isCreative && player.isShiftKeyDown) return
            Minecraft.getInstance().setScreen(ECBookScreen(type!!, ResearchBookData()))
        }
    }
}

@HCSubscribe
fun onCommandRegister(e: RegisterCommandsEvent) {
    ECCommands.register(e.dispatcher)
}

@HCSubscribe
fun onRegisterReloadListener(e: RegisterReloadListenersEvent.Server) {
    e.register(SoulStoneDataReloadListener(json))
    e.register(ConfigReloadListener())
    e.register(MagicTableIncreaseDataReloadListener(json))
}
