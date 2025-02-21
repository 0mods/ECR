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
import ru.hollowhorizon.hc.client.utils.literal
import ru.hollowhorizon.hc.client.utils.mcTranslate
import team._0mods.ecr.api.ModId
import team._0mods.ecr.api.item.BoundGem
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
fun onBoundGemTooltip(e: ItemTooltipEvent) {
    val stack = e.itemStack
    val item = stack.item
    val tooltip = e.toolTip

    if (item is BoundGem) {
        val pos = item.getBoundPos(stack)

        if (pos != null) {
            tooltip.add("tooltip.ecreimagined.bound_gem.at".mcTranslate.append(":").withStyle(ChatFormatting.GOLD))
            tooltip.add(
                "X".literal.withStyle(ChatFormatting.RED).append(": ")
                    .append("${pos.x}".literal).append(" ")
                    .append("Y".literal.withStyle(ChatFormatting.GREEN).append(": ")
                        .append("${pos.y}".literal).append(" "))
                    .append(
                        "Z".literal.withStyle(ChatFormatting.BLUE).append(": ")
                            .append("${pos.z}".literal))
            )

            if (!item.dimensionalBounds) {
                tooltip.add("tooltip.ecreimagined.bound_gem.dimension.unsupported".mcTranslate.withStyle(ChatFormatting.RED))
            }
        }
    }
}

@SubscribeEvent
fun onClientTick(e: ClientTickEvent) {
    if (e.phase != TickEvent.Phase.END) return

    //keys
}
