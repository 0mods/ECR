@file:Mod.EventBusSubscriber(Dist.CLIENT, modid = ModId, bus = Mod.EventBusSubscriber.Bus.FORGE)

package team._0mods.ecr.common.init.events.client

import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.world.item.SwordItem
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.event.entity.player.ItemTooltipEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import team._0mods.ecr.ModId
import team._0mods.ecr.common.items.ECBook
import team._0mods.ecr.common.items.ECBook.Companion.bookType
import team._0mods.ecr.common.utils.mru.MRUWeapon

@SubscribeEvent
fun onBookGMToolTip(e: ItemTooltipEvent) {
    val player = e.entity ?: return
    val stack = e.itemStack

    if (player.isCreative) {
        val item = stack.item
        if (item is ECBook) {
            val type = stack.bookType

            e.toolTip += Component.translatable(
                if (type != ECBook.Type.SHADE) "tooltip.$ModId.book.upgrade" else "tooltip.$ModId.book.downgrade",
                Component.literal("SHIFT + ")
                    .append(Component.translatable("tooltip.$ModId.rmc"))
            ).withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.ITALIC)
        }

        if (item is SwordItem && item is MRUWeapon) {
            val multiplier = item.multiplier
            e.toolTip += Component.literal(" ").append("$multiplier").append(" ").append(
                Component.translatable("tooltip.$ModId.sword_multiplier")
            ).withStyle(ChatFormatting.DARK_GREEN)
        }
    }
}