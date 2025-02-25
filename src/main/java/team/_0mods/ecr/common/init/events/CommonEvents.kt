package team._0mods.ecr.common.init.events

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import net.minecraft.client.Minecraft
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.ItemStack
import ru.hollowhorizon.hc.common.events.entity.player.PlayerInteractEvent
import ru.hollowhorizon.hc.common.events.item.BuildTabContentsEvent
import ru.hollowhorizon.hc.common.events.registry.RegisterCommandsEvent
import ru.hollowhorizon.hc.common.events.registry.RegisterReloadListenersEvent
import team._0mods.ecr.api.ModId
import team._0mods.ecr.api.item.HasSubItem
import team._0mods.ecr.client.screen.book.ECBookScreen
import team._0mods.ecr.common.api.NoTab
import team._0mods.ecr.common.data.ResearchBookData
import team._0mods.ecr.common.init.registry.ECCommands
import team._0mods.ecr.common.init.registry.ECRegistry
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

@HCSubscribe
fun onBuildCreativeTabs(e: BuildTabContentsEvent) {
    BuiltInRegistries.ITEM.filter { BuiltInRegistries.ITEM.getKey(it).namespace.contains(ModId) }.forEach {
        if (it is NoTab) return@forEach

        if (it is HasSubItem) {
            it.addSubItems(ItemStack(it)).forEach { stack -> e.acceptFor(ECRegistry.tabItems.get(), stack) }
            return@forEach
        }

        e.acceptFor(if (it is BlockItem) ECRegistry.tabBlocks.get() else ECRegistry.tabItems.get()) { it }
    }
}
