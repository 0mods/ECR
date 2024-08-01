@file:Mod.EventBusSubscriber(modid = ModId, bus = Mod.EventBusSubscriber.Bus.FORGE)

package team._0mods.ecr.common.init.events

import kotlinx.serialization.json.Json
import net.minecraft.client.Minecraft
import net.minecraftforge.event.AddReloadListenerEvent
import net.minecraftforge.event.RegisterCommandsEvent
import net.minecraftforge.event.entity.player.PlayerInteractEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import team._0mods.ecr.ModId
import team._0mods.ecr.client.screen.ECBookScreen
import team._0mods.ecr.common.init.registry.ECCommands
import team._0mods.ecr.common.init.registry.reload.ECStructureReloadListener
import team._0mods.ecr.common.init.registry.reload.SoulStoneDataReloadListener
import team._0mods.ecr.common.items.ECBook
import team._0mods.ecr.common.items.ECBook.Companion.bookType

private val json = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
    prettyPrint = true
    prettyPrintIndent = "  "
}

@SubscribeEvent
fun onBookUsed(e: PlayerInteractEvent.RightClickItem) {
    val player = e.entity
    val level = e.level
    val hand = e.hand
    val stack = player.getItemInHand(hand)
    val item = stack.item

    if (item is ECBook) {
        val type = stack.bookType
        if (level.isClientSide) {
            if (player.isCreative && player.isShiftKeyDown) return
            Minecraft.getInstance().setScreen(ECBookScreen(type))
        } else {
            if (player.isCreative && player.isShiftKeyDown) {
                when(type) {
                    ECBook.Type.BASIC -> stack.bookType = ECBook.Type.MRU
                    ECBook.Type.MRU -> stack.bookType = ECBook.Type.ENGINEER
                    ECBook.Type.ENGINEER -> stack.bookType = ECBook.Type.HOANA
                    ECBook.Type.HOANA -> stack.bookType = ECBook.Type.SHADE
                    ECBook.Type.SHADE -> stack.bookType = ECBook.Type.BASIC
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
}