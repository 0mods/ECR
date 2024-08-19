@file:Mod.EventBusSubscriber(Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)

package team._0mods.ecr.common.init.events.client

import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.world.item.SwordItem
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.event.TickEvent
import net.minecraftforge.event.TickEvent.ClientTickEvent
import net.minecraftforge.event.entity.player.ItemTooltipEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import team._0mods.ecr.ModId
import team._0mods.ecr.api.mru.MRUWeapon
import team._0mods.ecr.common.items.ECBook
import team._0mods.ecr.common.items.ECBook.Companion.bookType

@SubscribeEvent
fun onItemTooltip(e: ItemTooltipEvent) {
    val player = e.entity ?: return
    val stack = e.itemStack
    val item = stack.item

    if (player.isCreative) {
        if (item is ECBook) {
            val type = stack.bookType

            e.toolTip.add(Component.translatable(
                if (type != ECBook.Type.SHADE) "tooltip.$ModId.book.upgrade" else "tooltip.$ModId.book.downgrade",
                Component.literal("SHIFT + ")
                    .append(Component.translatable("tooltip.$ModId.rmc"))
            ).withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.ITALIC))
        }
    }

    if (item is SwordItem && item is MRUWeapon) {
        val multiplier = item.multiplier
        e.toolTip.add(Component.literal(" ").append("$multiplier").append(" ").append(
            Component.translatable("tooltip.$ModId.sword_multiplier")
        ).withStyle(ChatFormatting.DARK_GREEN))
    }
}

@SubscribeEvent
fun onClientTick(e: ClientTickEvent) {
    if (e.phase != TickEvent.Phase.END) return

    //keys
}
