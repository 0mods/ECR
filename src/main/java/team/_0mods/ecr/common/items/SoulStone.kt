package team._0mods.ecr.common.items

import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraftforge.event.entity.living.LivingDeathEvent
import team._0mods.ecr.ModId
import team._0mods.ecr.common.init.registry.ECTabs
import java.util.UUID

class SoulStone: Item(Properties().tab(ECTabs.tabItems)) {
    companion object {
        @get:JvmStatic
        val entityCapacityAdd = mutableMapOf<EntityType<*>, IntRange>()
        val defaultCapacityAdd = 40..100

        @JvmStatic
        fun isBounded(stack: ItemStack): Boolean {
            if (stack.item !is SoulStone) throw UnsupportedOperationException()
            val tag = stack.orCreateTag
            return tag.contains("SoulStoneOwner")
        }

        @JvmStatic
        fun getBoundedTo(stack: ItemStack): UUID? {
            if (stack.item !is SoulStone) throw UnsupportedOperationException()
            val tag = stack.orCreateTag
            return if (isBounded(stack)) tag.getUUID("SoulStoneOwner") else null
        }

        @JvmStatic
        fun setBoundedTo(stack: ItemStack, player: Player) {
            if (stack.item !is SoulStone) throw UnsupportedOperationException()
            val tag = stack.orCreateTag
            if (!isBounded(stack)) {
                tag.putUUID("SoulStoneOwner", player.uuid)
            }
        }

        @JvmStatic
        fun getCapacity(stack: ItemStack): Int {
            if (stack.item !is SoulStone) throw UnsupportedOperationException()
            val tag = stack.orCreateTag
            if (!tag.contains("SoulStoneCapacity")) {
                tag.putInt("SoulStoneCapacity", 0)
            }

            if (!isBounded(stack)) {
                tag.putInt("SoulStoneCapacity", 0)
            }

            return tag.getInt("SoulStoneCapacity")
        }

        @JvmStatic
        fun setCapacity(stack: ItemStack, count: Int) {
            if (stack.item !is SoulStone) throw UnsupportedOperationException()
            val tag = stack.orCreateTag

            tag.putInt("SoulStoneCapacity", count)
        }
    }

    override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack> {
        val stack = player.getItemInHand(usedHand)

        if (!player.isShiftKeyDown) {
            if (!isBounded(stack)) {
                setBoundedTo(stack, player)
                player.displayClientMessage(Component.translatable("info.$ModId.soul_stone.bounded", player.name), true)
                return InteractionResultHolder.success(stack)
            }
        }

        return super.use(level, player, usedHand)
    }

    private fun onEntityKill(e: LivingDeathEvent) {
        val source = e.source.entity ?: return
        if (source !is Player) return

    }
}
