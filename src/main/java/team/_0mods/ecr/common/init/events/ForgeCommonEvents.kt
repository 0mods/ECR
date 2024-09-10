@file:Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)

package team._0mods.ecr.common.init.events

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import net.minecraft.client.Minecraft
import net.minecraft.world.entity.player.Player
import net.minecraftforge.event.AddReloadListenerEvent
import net.minecraftforge.event.AttachCapabilitiesEvent
import net.minecraftforge.event.RegisterCommandsEvent
import net.minecraftforge.event.entity.player.PlayerInteractEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import team._0mods.ecr.client.screen.ECBookScreen
import team._0mods.ecr.common.init.registry.ECCommands
import team._0mods.ecr.common.init.registry.reload.ConfigReloadListener
import team._0mods.ecr.common.init.registry.reload.ECStructureReloadListener
import team._0mods.ecr.common.init.registry.reload.SoulStoneDataReloadListener
import team._0mods.ecr.common.items.ECBook
import team._0mods.ecr.common.items.ECBook.Companion.bookTypes

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

@SubscribeEvent
fun onBookUsed(e: PlayerInteractEvent.RightClickItem) {
    val player = e.entity
    val level = e.level
    val hand = e.hand
    val stack = player.getItemInHand(hand)
    val item = stack.item

    if (item is ECBook) {
        val type = stack.bookTypes
        if (level.isClientSide) {
            if (player.isCreative && player.isShiftKeyDown) return
            Minecraft.getInstance().setScreen(ECBookScreen(type!!))
        } else {
            if (player.isCreative && player.isShiftKeyDown) {
                type?.forEachIndexed { i, type ->
                    var bt = stack.bookTypes!!
                    if (i != bt.lastIndex) {
                        bt += type
                        stack.bookTypes = bt
                    } else {
                        stack.bookTypes = null
                    }
                }
            }
        }
    }
}

@SubscribeEvent
fun onCommandRegister(e: RegisterCommandsEvent) {
    ECCommands.register(e.dispatcher)
}

@SubscribeEvent
fun onRegisterReloadListener(e: AddReloadListenerEvent) {
    e.addListener(SoulStoneDataReloadListener(json))
    e.addListener(ECStructureReloadListener(json))
    e.addListener(ConfigReloadListener())
}

fun onCapabilityPlayerAttach(e: AttachCapabilitiesEvent<Player>) {
}
