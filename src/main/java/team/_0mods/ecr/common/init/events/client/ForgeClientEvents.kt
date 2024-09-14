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
import team._0mods.ecr.api.mru.MRUMultiplierWeapon

@SubscribeEvent
fun onItemTooltip(e: ItemTooltipEvent) {
    val stack = e.itemStack
    val item = stack.item

    if (item is SwordItem && item is MRUMultiplierWeapon) {
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
