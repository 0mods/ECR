package team._0mods.ecr.common.items

import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.monster.Enemy
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.SwordItem
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.entity.living.LivingDeathEvent
import team._0mods.ecr.ModId
import team._0mods.ecr.common.init.registry.ECTabs
import team._0mods.ecr.common.utils.MRUWeapon
import java.util.UUID

class SoulStone: Item(Properties().tab(ECTabs.tabItems)) {
    companion object {
        @get:JvmStatic
        val entityCapacityAdd = mutableMapOf<EntityType<*>, IntRange>()
        lateinit var defaultCapacityAdd: IntRange
        lateinit var defaultEnemyAdd: IntRange

        @Deprecated("Deprecated")
        @JvmStatic
        val ItemStack.isBounded: Boolean get() {
            if (this.item !is SoulStone) throw UnsupportedOperationException()
            val tag = this.orCreateTag
            return tag.contains("SoulStoneOwner")
        }

        @JvmStatic
        var ItemStack.boundedTo: UUID? get() {
            if (this.item !is SoulStone) throw UnsupportedOperationException()
            val tag = this.orCreateTag
            return if (isBounded) tag.getUUID("SoulStoneOwner") else null
        }
            set(value) {
                if (this.item !is SoulStone) throw UnsupportedOperationException()
                val tag = this.orCreateTag

                if (!isBounded) {
                    tag.putUUID("SoulStoneOwner", value!!)
                } else {
                    tag.remove("SoulStoneOwner")
                    tag.remove("SoulStoneCapacity")
                }
            }

        @JvmStatic
        var ItemStack.capacity: Int
            get() {
                if (this.item !is SoulStone) throw UnsupportedOperationException()
                val tag = this.orCreateTag
                if (!isBounded) {
                    tag.putInt("SoulStoneCapacity", 0)
                }

                return tag.getInt("SoulStoneCapacity")
            }
            set(value) {
                if (this.item !is SoulStone) throw UnsupportedOperationException()
                val tag = this.orCreateTag

                tag.putInt("SoulStoneCapacity", value)
            }

        fun ItemStack.add(count: Int) {
            if (this.boundedTo != null) {
                val cap = this.capacity
                this.capacity = cap + count
            }
        }
    }

    init {
        MinecraftForge.EVENT_BUS.addListener(this::onEntityKill)
    }

    override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack> {
        val stack = player.getItemInHand(usedHand)

        if (!player.isShiftKeyDown) {
            if (!stack.isBounded) {
                if (stack.count > 1) {
                    val copiedStack = stack.copy().apply {
                        count = 1
                        boundedTo = player.uuid
                    }
                    stack.shrink(1)

                    val ent = ItemEntity(level, player.x, player.y, player.z, copiedStack).apply {
                        setNoPickUpDelay()
                    }

                    level.addFreshEntity(ent)
                } else {
                    stack.boundedTo = player.uuid
                }

                player.displayClientMessage(Component.translatable("info.$ModId.soul_stone.bounded", player.name), true)
                return InteractionResultHolder.success(stack)
            }
        } else {
            if (stack.isBounded) {
                 if (stack.boundedTo != player.uuid) {
                     player.displayClientMessage(Component.translatable("info.$ModId.soul_stone.can_not_unbound"), true)
                     return InteractionResultHolder.fail(stack)
                 } else {
                     stack.boundedTo = null
                     player.displayClientMessage(Component.translatable("info.$ModId.soul_stone.unbounded"), true)
                     return InteractionResultHolder.fail(stack)
                 }
            }
        }

        return super.use(level, player, usedHand)
    }

    override fun getMaxStackSize(stack: ItemStack?): Int {
        if (stack != null && stack.hasTag()) return 1

        return super.getMaxStackSize(stack)
    }

    override fun appendHoverText(
        stack: ItemStack,
        level: Level?,
        tooltipComponents: MutableList<Component>,
        isAdvanced: TooltipFlag
    ) {
        level ?: return

        if (stack.isBounded) {
            val player = level.getPlayerByUUID(stack.boundedTo!!)

            tooltipComponents += Component.translatable(
                "tooltip.$ModId.soul_stone.tracking",
                (player?.name as? MutableComponent)?.withStyle(ChatFormatting.GOLD) ?:
                Component.literal("Not Loaded").withStyle(ChatFormatting.GOLD)
            ).withStyle(ChatFormatting.DARK_GRAY)

            tooltipComponents += Component.translatable(
                "tooltip.$ModId.soul_stone.detected_ubmru",
                Component.literal(stack.capacity.toString()).withStyle(ChatFormatting.GREEN)
            ).withStyle(ChatFormatting.DARK_GRAY)
        }
    }

    private fun onEntityKill(e: LivingDeathEvent) {
        val source = e.source.entity ?: return
        val ent = e.entity
        if (source !is Player) return

        val weapon = source.getItemInHand(InteractionHand.MAIN_HAND).item
        val multiplier = if (weapon is MRUWeapon && weapon is SwordItem) weapon.multiplier else 1

        val item = source.inventory.items.filter { it.item is SoulStone && it.boundedTo == source.uuid }[0]

        if (ent.isBaby && ent !is Enemy) return

        if (entityCapacityAdd.contains(ent.type)) item.add(entityCapacityAdd[ent.type]!!.random() * multiplier)
        else {
            if (ent is Enemy) item.add(defaultEnemyAdd.random() * multiplier)
            else item.add(defaultCapacityAdd.random() * multiplier)
        }
    }
}
