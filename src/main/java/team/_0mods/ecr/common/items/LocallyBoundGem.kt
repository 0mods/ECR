package team._0mods.ecr.common.items

import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Rarity
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level
import team._0mods.ecr.api.ModId
import team._0mods.ecr.api.mru.MRUGenerator
import team._0mods.ecr.common.init.registry.ECTabs
import team._0mods.ecr.api.item.BoundGem

class LocallyBoundGem : Item(Properties().tab(ECTabs.tabItems)), BoundGem {
    companion object {
        var ItemStack.boundPos: BlockPos?
            get() {
                if (this.item !is LocallyBoundGem) throw UnsupportedOperationException()
                return (this.item as BoundGem).getBlockPos(this)
            }
            set(value) {
                if (this.item !is LocallyBoundGem) throw UnsupportedOperationException()
                (this.item as BoundGem).setBlockPos(this, value)
            }
    }

    override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack> {
        val stack = player.getItemInHand(usedHand)

        if (player.isShiftKeyDown) {
            if (stack.boundPos != null) {
                player.displayClientMessage(Component.translatable("tooltip.$ModId.bound_gem.unbound"), true)
                stack.boundPos = null
                return InteractionResultHolder.success(stack)
            }
        }

        return super.use(level, player, usedHand)
    }

    override fun useOn(context: UseOnContext): InteractionResult {
        val player = context.player ?: return InteractionResult.FAIL
        val stack = context.itemInHand
        val level = context.level
        val pos = context.clickedPos

        val blockEntity = level.getBlockEntity(pos)

        if (blockEntity != null && blockEntity is MRUGenerator) {
            if (stack.boundPos == null) {
                player.displayClientMessage(Component.translatable("tooltip.$ModId.bound_gem.bound", pos.x, pos.y, pos.z), true)
                if (stack.count > 1) {
                    val copiedStack = stack.copy().apply {
                        count = 1
                        boundPos = pos
                    }

                    stack.shrink(1)

                    val ent = ItemEntity(level, player.x, player.y, player.z, copiedStack).apply {
                        setNoPickUpDelay()
                    }

                    level.addFreshEntity(ent)
                } else {
                    stack.boundPos = pos
                }
                return InteractionResult.SUCCESS
            }
        }

        return super.useOn(context)
    }

    override fun getRarity(stack: ItemStack): Rarity {
        return if (stack.boundPos != null) Rarity.EPIC else super.getRarity(stack)
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
        val pos = stack.boundPos

        if (pos != null) {
            tooltipComponents.add(Component.literal("${pos.x} ${pos.y} ${pos.z}"))
        }
    }
}