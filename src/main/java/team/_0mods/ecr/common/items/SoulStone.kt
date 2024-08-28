package team._0mods.ecr.common.items

import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
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
import team._0mods.ecr.api.mru.MRUWeapon
import team._0mods.ecr.common.init.registry.ECTabs
import java.util.*
import kotlin.math.roundToInt

class SoulStone: Item(Properties().tab(ECTabs.tabItems)) {
    companion object {
        @get:JvmStatic
        val entityCapacityAdd = mutableMapOf<EntityType<*>, IntRange>()
        lateinit var defaultCapacityAdd: IntRange
        lateinit var defaultEnemyAdd: IntRange
    }

    @get:JvmName("privateOwnerGet")
    @set:JvmName("privateOwnerSet")
    private var ItemStack.owner
        get() = getOwner(this)
        set(value) = setOwner(this, value)

    init {
        MinecraftForge.EVENT_BUS.addListener(this::onEntityKill)
    }

    override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack> {
        val stack = player.getItemInHand(usedHand)

        if (!level.isClientSide) {
            if (!player.isShiftKeyDown) {
                if (stack.owner == null) {
                    if (stack.count > 1) {
                        val copiedStack = stack.copy().apply {
                            count = 1
                            owner = player.uuid
                        }
                        stack.shrink(1)

                        val ent = ItemEntity(level, player.x, player.y, player.z, copiedStack).apply {
                            setNoPickUpDelay()
                            this.owner = player.uuid
                        }

                        level.addFreshEntity(ent)
                    } else {
                        stack.owner = player.uuid
                        setOwnerNick(stack, player.name.string)
                    }

                    player.displayClientMessage(
                        Component.translatable("info.$ModId.soul_stone.bounded", player.name),
                        true
                    )
                    return InteractionResultHolder.success(stack)
                }
            } else {
                if (stack.owner != null) {
                    if (stack.owner != player.uuid) {
                        player.displayClientMessage(
                            Component.translatable("info.$ModId.soul_stone.can_not_unbound"),
                            true
                        )
                        return InteractionResultHolder.fail(stack)
                    } else {
                        stack.owner = null
                        setOwnerNick(stack, null)
                        player.displayClientMessage(Component.translatable("info.$ModId.soul_stone.unbounded"), true)
                        return InteractionResultHolder.fail(stack)
                    }
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

        if (stack.owner != null) {
            tooltipComponents.add(
                Component.translatable(
                    "tooltip.$ModId.soul_stone.tracking",
                    Component.literal(getOwnerNick(stack)).withStyle(if (getOwnerNick(stack) == "Not Loaded") ChatFormatting.RED else ChatFormatting.GOLD)
                ).withStyle(ChatFormatting.DARK_GRAY)
            )

            tooltipComponents.add(
                Component.translatable(
                    "tooltip.$ModId.soul_stone.detected_ubmru",
                    Component.literal(this.getCapacity(stack).toString()).withStyle(ChatFormatting.GREEN)
                ).withStyle(ChatFormatting.DARK_GRAY)
            )
        }
    }

    private fun onEntityKill(e: LivingDeathEvent) {
        val source = e.source.entity ?: return
        val ent = e.entity
        if (source !is Player) return

        val items = source.inventory.items.filter { it.item is SoulStone }

        if (items.isEmpty()) return

        val item = items.random()

        if (item.owner == null) return
        if (ent.isBaby && ent !is Enemy) return

        val weapon = source.getItemInHand(InteractionHand.MAIN_HAND).item
        val multiplier = if (weapon is MRUWeapon && weapon is SwordItem) weapon.multiplier else 1f

        if (entityCapacityAdd.contains(ent.type)) {
            val a = entityCapacityAdd[ent.type]!!.random() * multiplier
            this.add(item, a)
        } else {
            if (ent is Enemy) {
                val a = defaultEnemyAdd.random() * multiplier
                this.add(item, a)
            } else {
                val a = defaultCapacityAdd.random() * multiplier
                this.add(item, a)
            }
        }
    }

    fun getOwner(stack: ItemStack): UUID? {
        if (stack.item !is SoulStone) throw UnsupportedOperationException()
        val tag = stack.orCreateTag
        return if (tag.contains("SoulStoneOwner")) {
            try {
                tag.getUUID("SoulStoneOwner")
            } catch (e: Exception) {
                null
            }
        } else null
    }

    fun setOwner(stack: ItemStack, newOwner: UUID?) {
        if (stack.item !is SoulStone) throw UnsupportedOperationException()
        val tag = stack.orCreateTag

        if (!tag.contains("SoulStoneOwner")) {
            if (newOwner != null)
                tag.putUUID("SoulStoneOwner", newOwner)
        } else {
            if (newOwner == null) {
                tag.remove("SoulStoneOwner")
                tag.remove("SoulStoneCapacity")
            }
        }
    }

    private fun getOwnerNick(stack: ItemStack): String {
        if (stack.item !is SoulStone) throw UnsupportedOperationException()
        val tag = stack.orCreateTag
        return if (tag.contains("SoulStoneOwnerName")) {
            try {
                tag.getString("SoulStoneOwnerName")
            } catch (e: Exception) {
                "Not Loaded"
            }
        } else "Not Loaded"
    }

    private fun setOwnerNick(stack: ItemStack, name: String?) {
        if (stack.item !is SoulStone) throw UnsupportedOperationException()
        val tag = stack.orCreateTag

        if (name != null) {
            tag.putString("SoulStoneOwnerName", name)
        } else {
            tag.remove("SoulStoneOwnerName")
        }
    }

    fun getCapacity(stack: ItemStack): Int {
        if (stack.item !is SoulStone) throw UnsupportedOperationException()
        val tag = stack.orCreateTag
        if (stack.owner == null) {
            tag.remove("SoulStoneCapacity")
            return 0
        }

        return tag.getInt("SoulStoneCapacity")
    }

    fun setCapacity(stack: ItemStack, newCapacity: Int) {
        if (stack.item !is SoulStone) throw UnsupportedOperationException()
        val tag = stack.orCreateTag

        if (stack.owner != null) tag.putInt("SoulStoneCapacity", newCapacity)
    }

    fun add(stack: ItemStack, count: Float) {
        val conv = count.roundToInt()
        if (stack.owner != null) {
            val cap = getCapacity(stack)
            setCapacity(stack, cap + conv)
        }
    }

    fun add(stack: ItemStack, count: Double) {
        val conv = count.roundToInt()
        if (stack.owner != null) {
            val cap = getCapacity(stack)
            setCapacity(stack, cap + conv)
        }
    }

    fun remove(stack: ItemStack, count: Int) {
        val cap = getCapacity(stack)
        if (stack.owner != null) {
            if (cap - count > 0) {
                setCapacity(stack, cap - count)
            } else {
                setCapacity(stack, 0)
            }
        }
    }
}
