package team._0mods.ecr.common.items

import net.minecraft.ChatFormatting
import net.minecraft.nbt.CompoundTag
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
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.event.entity.living.LivingDeathEvent
import team._0mods.ecr.ModId
import team._0mods.ecr.api.mru.MRUWeapon
import team._0mods.ecr.common.capability.impl.SoulStoneMRUContainer
import team._0mods.ecr.common.init.registry.ECCapabilities
import team._0mods.ecr.common.init.registry.ECTabs
import java.util.*
import kotlin.math.roundToInt

class SoulStone: Item(Properties().tab(ECTabs.tabItems)) {
    companion object {
        @get:JvmStatic
        val entityCapacityAdd = mutableMapOf<EntityType<*>, IntRange>()
        lateinit var defaultCapacityAdd: IntRange
        lateinit var defaultEnemyAdd: IntRange

        @JvmStatic
        var ItemStack.owner: UUID?
            get() {
                if (this.item !is SoulStone) throw UnsupportedOperationException()
                val tag = this.orCreateTag
                return if (tag.contains("SoulStoneOwner")) {
                    try {
                        tag.getUUID("SoulStoneOwner")
                    } catch (e: Exception) {
                        null
                    }
                } else null
            }
            set(value) {
                if (this.item !is SoulStone) throw UnsupportedOperationException()
                val tag = this.orCreateTag

                if (!tag.contains("SoulStoneOwner")) {
                    tag.putUUID("SoulStoneOwner", value!!)
                } else {
                    if (value == null) {
                        tag.remove("SoulStoneOwner")
                        this.invalidateCaps()
                    }
                }
            }
    }

    init {
        MinecraftForge.EVENT_BUS.addListener(this::onEntityKill)
    }

    override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack> {
        val stack = player.getItemInHand(usedHand)

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
                    }

                    level.addFreshEntity(ent)
                } else {
                    stack.owner = player.uuid
                }

                player.displayClientMessage(Component.translatable("info.$ModId.soul_stone.bounded", player.name), true)
                return InteractionResultHolder.success(stack)
            }
        } else {
            if (stack.owner != null) {
                 if (stack.owner != player.uuid) {
                     player.displayClientMessage(Component.translatable("info.$ModId.soul_stone.can_not_unbound"), true)
                     return InteractionResultHolder.fail(stack)
                 } else {
                     stack.owner = null
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

        if (stack.owner != null) {
            val player = level.getPlayerByUUID(stack.owner!!)

            tooltipComponents.add(Component.translatable(
                "tooltip.$ModId.soul_stone.tracking",
                (player?.name as? MutableComponent)?.withStyle(ChatFormatting.GOLD) ?:
                Component.literal("Not Loaded").withStyle(ChatFormatting.RED)
            ).withStyle(ChatFormatting.DARK_GRAY))

            stack.getCapability(ECCapabilities.MRU_CONTAINER).ifPresent {
                tooltipComponents.add(Component.translatable(
                    "tooltip.$ModId.soul_stone.detected_ubmru",
                    Component.literal(it.mruStorage.toString()).withStyle(ChatFormatting.GREEN)
                ).withStyle(ChatFormatting.DARK_GRAY))
            }
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
            val cap = item.getCapability(ECCapabilities.MRU_CONTAINER).orElseThrow { NullPointerException("Capability is null") }
            cap.receiveMru(a.roundToInt())
        } else {
            if (ent is Enemy) {
                val a = defaultEnemyAdd.random() * multiplier
                val cap = item.getCapability(ECCapabilities.MRU_CONTAINER).orElseThrow { NullPointerException("Capability is null") }
                cap.receiveMru(a.roundToInt())
            } else {
                val a = defaultCapacityAdd.random() * multiplier
                val cap = item.getCapability(ECCapabilities.MRU_CONTAINER).orElseThrow { NullPointerException("Capability is null") }
                cap.receiveMru(a.roundToInt())
            }
        }
    }

    override fun initCapabilities(stack: ItemStack?, nbt: CompoundTag?): ICapabilityProvider = SoulStoneMRUContainer()
}
