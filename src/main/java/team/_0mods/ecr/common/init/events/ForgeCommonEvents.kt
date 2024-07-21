package team._0mods.ecr.common.init.events

import net.minecraft.client.Minecraft
import net.minecraftforge.event.RegisterCommandsEvent
import net.minecraftforge.event.entity.player.PlayerInteractEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import team._0mods.ecr.ModId
import team._0mods.ecr.client.ECBookScreen
import team._0mods.ecr.common.command.ECCommands
import team._0mods.ecr.common.items.ECBook

@Mod.EventBusSubscriber(modid = ModId, bus = Mod.EventBusSubscriber.Bus.FORGE)
class ForgeCommonEvents {
    @SubscribeEvent
    fun onBookUsed(e: PlayerInteractEvent.RightClickItem) {
        val player = e.entity
        val level = e.level
        val hand = e.hand
        val stack = player.getItemInHand(hand)
        val item = stack.item

        if (item is ECBook) {
            val type = ECBook.getBookType(stack)
            if (level.isClientSide) {
                if (player.isCreative && player.isShiftKeyDown) return
                Minecraft.getInstance().setScreen(ECBookScreen(type))
            } else {
                if (player.isCreative && player.isShiftKeyDown) {
                    when(type) {
                        ECBook.Type.BASIC -> ECBook.setBookType(stack, ECBook.Type.MRU)
                        ECBook.Type.MRU -> ECBook.setBookType(stack, ECBook.Type.ENGINEER)
                        ECBook.Type.ENGINEER -> ECBook.setBookType(stack, ECBook.Type.HOANA)
                        ECBook.Type.HOANA -> ECBook.setBookType(stack, ECBook.Type.SHADE)
                        ECBook.Type.SHADE -> ECBook.setBookType(stack, ECBook.Type.BASIC)
                    }
                }
            }
        }
    }

    @SubscribeEvent
    fun onCommandRegister(e: RegisterCommandsEvent) {
        ECCommands.register(e.dispatcher)
    }
}