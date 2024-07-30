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
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level
import net.minecraftforge.fml.loading.FMLEnvironment
import team._0mods.ecr.common.utils.MRUGenerator
import team._0mods.ecr.common.init.registry.ECTabs

class BoundGem : Item(Properties().tab(ECTabs.tabItems)) {
    companion object {
        var ItemStack.boundPos: BlockPos?
            get() {
                if (this.item !is BoundGem) throw UnsupportedOperationException()
                val tag = this.orCreateTag
                if (!tag.contains("BoundGemX") && !tag.contains("BoundGemY") && !tag.contains("BoundGemZ")) return null
                return BlockPos(tag.getInt("BoundGemX"), tag.getInt("BoundGemY"), tag.getInt("BoundGemZ"))
            }
            set(value) {
                if (this.item !is BoundGem) throw UnsupportedOperationException()
                val tag = this.orCreateTag

                if (value == null) {
                    this.tag = null
                } else {
                    tag.putInt("BoundGemX", value.x)
                    tag.putInt("BoundGemY", value.y)
                    tag.putInt("BoundGemZ", value.z)
                }
            }
    }

    override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack> {
        val stack = player.getItemInHand(usedHand)

        if (player.isShiftKeyDown) {
            if (stack.boundPos != null) {
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

        val block = level.getBlockState(pos).block

        if (block is MRUGenerator || !FMLEnvironment.production) {
            if (stack.boundPos == null) {
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
            tooltipComponents += Component.literal("${pos.x} ${pos.y} ${pos.z}")
        }
    }
}