package com.algorithmlx.ecr.common.init.events

import com.algorithmlx.ecr.api.ModId
import com.algorithmlx.ecr.api.item.BoundGem
import com.algorithmlx.ecr.api.item.SoulStoneLike
import com.algorithmlx.ecr.api.recipe.CachedRecipe
import com.algorithmlx.ecr.api.utils.countByIngredient
import com.algorithmlx.ecr.common.components.SoulStoneComponent
import com.algorithmlx.ecr.common.init.registry.DataComponentRegistry
import com.algorithmlx.ecr.common.recipe.StructureRecipe
import net.minecraft.ChatFormatting
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.SingleRecipeInput
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.phys.Vec3

object ECEvents {
    @JvmStatic
    fun itemTooltip(stack: ItemStack, tooltips: MutableList<Component>) {
        when (stack.item) {
            is SoulStoneLike -> {
                val component = stack.getOrDefault(DataComponentRegistry.instance.soulStone, SoulStoneComponent.EMPTY)

                if (component == SoulStoneComponent.EMPTY) return

                tooltips += if (component.ownerName.isNotEmpty())
                    Component.translatable(
                        "tooltip.$ModId.soul_stone.tracking",
                        Component.literal(component.ownerName).withStyle(ChatFormatting.GOLD)
                    ).withStyle(ChatFormatting.DARK_GRAY)
                else Component.translatable("tooltip.$ModId.soul_stone.error").withStyle(ChatFormatting.DARK_RED)

                tooltips += Component.translatable(
                    "tooltip.$ModId.soul_stone.detected_ubmru",
                    Component.literal(component.capacity.toString()).withStyle(ChatFormatting.GREEN)
                ).withStyle(ChatFormatting.DARK_GRAY)
            }

            is BoundGem -> {

            }
        }
    }

    @JvmStatic
    fun itemEntityTickCraft(
        stack: ItemStack, cached: CachedRecipe<SingleRecipeInput, StructureRecipe>, pos: Vec3,
        level: Level, timer: IntArray
    ) {
        val center = BlockPos.containing(pos).below()

        val craftingInput = SingleRecipeInput(stack)
        val recipe = cached.testAndGet(craftingInput, level) ?: return

        val state = level.getBlockState(center)
        val isAtCenter = recipe.structureCenter?.let { state.`is`(it) } ?: true

        val placement = recipe.multiblock.findPlacement(level, center)
        if (!isAtCenter || placement == null) {
            timer[0] = 0
            return
        }

        if (!level.getBlockState(center.above()).`is`(Blocks.AIR)) return

        val place = recipe.blockForPlace
        val result = recipe.assemble(craftingInput)

        timer[0] = timer[0] + 1
        if (timer[0] < recipe.time) return

        timer[0] = 0

        stack.shrink(countByIngredient(recipe.ingredient))

        if (recipe.consumeStructure) recipe.multiblock.replaceInWorld(level, placement) { Blocks.AIR.defaultBlockState() }

        if (!(recipe.chance.isEmpty() || level.random.nextInt(recipe.chance.max) >= recipe.chance.min)) return

        if (place != null) {
            level.setBlock(
                center.above(),
                place.defaultBlockState(),
                Block.UPDATE_NEIGHBORS or Block.UPDATE_CLIENTS or Block.UPDATE_SUPPRESS_DROPS
            )
        } else {
            val item = ItemEntity(level, pos.x, pos.y, pos.z, result).apply { this.setNoPickUpDelay() }
            level.addFreshEntity(item)
        }
    }
}
