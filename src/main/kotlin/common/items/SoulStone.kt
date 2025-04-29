package team._0mods.ecr.common.items

import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level
import ru.hollowhorizon.hc.common.utils.get
import ru.hollowhorizon.hc.common.utils.literal
import ru.hollowhorizon.hc.common.utils.mcTranslate
import team._0mods.ecr.api.ModId
import team._0mods.ecr.api.item.SoulStoneLike
import team._0mods.ecr.api.utils.SoulStoneUtils.capacity
import team._0mods.ecr.api.utils.SoulStoneUtils.isCreative
import team._0mods.ecr.api.utils.SoulStoneUtils.matrix
import team._0mods.ecr.api.utils.SoulStoneUtils.owner
import team._0mods.ecr.api.utils.SoulStoneUtils.ownerName
import team._0mods.ecr.common.capability.PlayerMRU
import team._0mods.ecr.commonConfig

class SoulStone: Item(Properties()), SoulStoneLike {
    companion object {
        @get:JvmStatic
        val entityCapacityAdd = mutableMapOf<EntityType<*>, IntRange>()
        lateinit var defaultCapacityAdd: IntRange
        lateinit var defaultEnemyAdd: IntRange
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

            stack.matrix?.let {
                tooltipComponents.add("tooltip.$ModId.soul_stone.matrix".mcTranslate(it.displayName).withStyle(ChatFormatting.DARK_GRAY))
            }
        }

        if (stack.isCreative)
            tooltipComponents.add("tooltip.${ModId}.soul_stone.creative".mcTranslate.withStyle(ChatFormatting.DARK_PURPLE))
    }

    override fun inventoryTick(stack: ItemStack, level: Level, entity: Entity, slotId: Int, isSelected: Boolean) {
        if (stack.item !is SoulStone) return
        if (!level.isClientSide) {
            val server = level.server!!
            val uuid = stack.owner

            if (uuid != null) {
                val player = server.playerList.getPlayer(uuid)
                player?.let {
                    if (stack.ownerName.isNullOrEmpty())
                        stack.ownerName = it.displayName.string

                    stack.matrix = it[PlayerMRU::class].getMatrixType()
                }
            } else if (stack.matrix != null) {
                stack.matrix = null
            }
        }
    }

    override val receiveCount: Int = commonConfig.soulStoneConfig.output
    override val extractCount: Int = commonConfig.soulStoneConfig.input
}
