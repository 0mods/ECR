package team._0mods.ecr.common.init.events

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import net.minecraft.client.Minecraft
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.monster.Enemy
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.SwordItem
import ru.hollowhorizon.hc.common.events.entity.LivingEntityDeathEvent
import ru.hollowhorizon.hc.common.events.entity.player.PlayerInteractEvent
import ru.hollowhorizon.hc.common.events.item.BuildTabContentsEvent
import ru.hollowhorizon.hc.common.events.registry.RegisterCommandsEvent
import ru.hollowhorizon.hc.common.events.registry.RegisterReloadListenersEvent
import team._0mods.ecr.api.ModId
import team._0mods.ecr.api.item.HasSubItem
import team._0mods.ecr.api.item.SoulStoneLike
import team._0mods.ecr.api.mru.MRUMultiplierWeapon
import team._0mods.ecr.api.utils.SoulStoneUtils.addUBMRU
import team._0mods.ecr.api.utils.SoulStoneUtils.isCreative
import team._0mods.ecr.api.utils.SoulStoneUtils.owner
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
import team._0mods.ecr.common.items.SoulStone.Companion.defaultCapacityAdd
import team._0mods.ecr.common.items.SoulStone.Companion.defaultEnemyAdd
import team._0mods.ecr.common.items.SoulStone.Companion.entityCapacityAdd
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

@HCSubscribe
fun onEntityDeath(e: LivingEntityDeathEvent) {
    val source = e.source.entity ?: return
    val ent = e.entity
    if (source !is Player) return

    val items = source.inventory.items.filter { it.item is SoulStoneLike }

    if (items.isEmpty()) return

    val item = items.random()

    item.owner ?: return

    if (item.isCreative) return
    if (ent.isBaby && ent !is Enemy) return

    val weapon = source.getItemInHand(InteractionHand.MAIN_HAND).item
    val multiplier = if (weapon is MRUMultiplierWeapon && weapon is SwordItem) weapon.multiplier else 1f

    val addCount = if (entityCapacityAdd.contains(ent.type))
        entityCapacityAdd[ent.type]!!.random() * multiplier
    else {
        if (ent is Enemy) defaultEnemyAdd.random() * multiplier
        else defaultCapacityAdd.random() * multiplier
    }

    item.addUBMRU(addCount)
}
