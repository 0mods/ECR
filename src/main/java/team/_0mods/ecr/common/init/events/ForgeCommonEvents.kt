@file:Mod.EventBusSubscriber(modid = ModId, bus = Mod.EventBusSubscriber.Bus.FORGE)

package team._0mods.ecr.common.init.events

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import net.minecraft.client.Minecraft
import net.minecraftforge.event.AddReloadListenerEvent
import net.minecraftforge.event.RegisterCommandsEvent
import net.minecraftforge.event.entity.player.PlayerInteractEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.ModList
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.registries.ForgeRegistries
import team._0mods.ecr.LOGGER
import team._0mods.ecr.ModId
import team._0mods.ecr.client.ECBookScreen
import team._0mods.ecr.common.command.ECCommands
import team._0mods.ecr.common.data.SoulStoneData
import team._0mods.ecr.common.init.registry.reload.SoulStoneDataReloadListener
import team._0mods.ecr.common.items.ECBook
import team._0mods.ecr.common.items.ECBook.Companion.bookType
import team._0mods.ecr.common.items.SoulStone
import team._0mods.ecr.common.rl
import java.util.concurrent.CompletableFuture

private val json = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
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
    LOGGER.info("Registering Commands")
    ECCommands.register(e.dispatcher)
}

@SubscribeEvent
fun onRegisterReloadListener(e: AddReloadListenerEvent) {
    LOGGER.info("Registering Reload Listener")
    e.addListener(SoulStoneDataReloadListener(json))
}