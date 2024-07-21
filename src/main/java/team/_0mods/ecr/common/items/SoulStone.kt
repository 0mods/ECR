package team._0mods.ecr.common.items

import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import team._0mods.ecr.common.init.registry.ECTabs
import java.util.UUID

class SoulStone: Item(Properties().tab(ECTabs.tabItems)) {
    init {
        fun isBounded(stack: ItemStack): Boolean {
            val tag = stack.orCreateTag
            return tag.contains("SoulStoneOwner")
        }

        fun getBoundedTo(stack: ItemStack): UUID? {
            val tag = stack.orCreateTag
            return if (isBounded(stack)) tag.getUUID("SoulStoneOwner") else null
        }

        fun setBoundedTo(stack: ItemStack, player: Player) {
            val tag = stack.orCreateTag
            if (!isBounded(stack)) {
                tag.putUUID("SoulStoneOwner", player.uuid)
            }
        }
    }
}
