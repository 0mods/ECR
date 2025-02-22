package team._0mods.ecr.common.items

import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.Entity
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
import ru.hollowhorizon.hc.client.utils.get
import ru.hollowhorizon.hc.client.utils.literal
import ru.hollowhorizon.hc.client.utils.mcTranslate
import team._0mods.ecr.api.ModId
import team._0mods.ecr.api.item.SoulStoneLike
import team._0mods.ecr.api.mru.MRUMultiplierWeapon
import team._0mods.ecr.api.utils.SoulStoneUtils.addUBMRU
import team._0mods.ecr.api.utils.SoulStoneUtils.capacity
import team._0mods.ecr.api.utils.SoulStoneUtils.isCreative
import team._0mods.ecr.api.utils.SoulStoneUtils.matrix
import team._0mods.ecr.api.utils.SoulStoneUtils.owner
import team._0mods.ecr.api.utils.SoulStoneUtils.ownerName
import team._0mods.ecr.common.capability.PlayerMRU
import team._0mods.ecr.common.init.config.ECCommonConfig

class SoulStone: Item(Properties()), SoulStoneLike {
    companion object {
        @get:JvmStatic
        val entityCapacityAdd = mutableMapOf<EntityType<*>, IntRange>()
        lateinit var defaultCapacityAdd: IntRange
        lateinit var defaultEnemyAdd: IntRange
    }

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
                            this.setThrower(player.uuid)
                        }

                        level.addFreshEntity(ent)
                    } else {
                        stack.owner = player.uuid
                    }

                    player.displayClientMessage(
                        Component.translatable("tooltip.$ModId.soul_stone.bounded", player.name),
                        true
                    )
                    return InteractionResultHolder.success(stack)
                }
            } else {
                if (stack.owner != null) {
                    if (stack.owner != player.uuid) {
                        player.displayClientMessage(
                            Component.translatable("tooltip.$ModId.soul_stone.can_not_unbound"),
                            true
                        )
                        return InteractionResultHolder.fail(stack)
                    } else {
                        stack.owner = null
                        player.displayClientMessage(Component.translatable("tooltip.$ModId.soul_stone.unbounded"), true)
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
        if (stack.owner != null) {
            val name = stack.ownerName!!
            tooltipComponents.add(
                "tooltip.$ModId.soul_stone.tracking".mcTranslate(
                    name.literal.withStyle(if (name == "Not Loaded") ChatFormatting.RED else ChatFormatting.GOLD)
                ).withStyle(ChatFormatting.DARK_GRAY)
            )

            if (!stack.isCreative)
                tooltipComponents.add(
                    "tooltip.$ModId.soul_stone.detected_ubmru"
                        .mcTranslate(stack.capacity.toString().literal.withStyle(ChatFormatting.GREEN))
                        .withStyle(ChatFormatting.DARK_GRAY)
                )
            else tooltipComponents.add("tooltip.${ModId}.soul_stone.creative".mcTranslate.withStyle(ChatFormatting.DARK_PURPLE))

            stack.matrix?.let {
                tooltipComponents.add("tooltip.$ModId.soul_stone.matrix".mcTranslate(it.displayName).withStyle(ChatFormatting.DARK_GRAY))
            }
        }
    }

    override fun inventoryTick(stack: ItemStack, level: Level, entity: Entity, slotId: Int, isSelected: Boolean) {
        if (stack.item !is SoulStone) return
        if (!level.isClientSide) {
            val server = level.server!!
            val uuid = stack.owner
            if (uuid != null) {
                val player = server.playerList.getPlayer(uuid)
                player?.let { stack.matrix = it[PlayerMRU::class].getMatrixType() }
            } else if (stack.matrix != null) {
                stack.matrix = null
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

        item.owner ?: return

        if (item.isCreative) return
        if (ent.isBaby && ent !is Enemy) return

        val weapon = source.getItemInHand(InteractionHand.MAIN_HAND).item
        val multiplier = if (weapon is MRUMultiplierWeapon && weapon is SwordItem) weapon.multiplier else 1f

        val addCount = if (entityCapacityAdd.contains(ent.type))
            entityCapacityAdd[ent.type]!!.random() * multiplier
        else {
            if (ent is Enemy) defaultEnemyAdd.random() * multiplier
            else defaultCapacityAdd.random() * multiplier
        }

        item.addUBMRU(addCount)
    }

    override val receiveCount: Int = ECCommonConfig.instance.soulStoneReceiveCount
    override val extractCount: Int = ECCommonConfig.instance.soulStoneExtractCount
}
