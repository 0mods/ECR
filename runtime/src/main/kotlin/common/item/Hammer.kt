package com.algorithmlx.ecr.common.item

import com.algorithmlx.ecr.api.assembled.AssembledMultiblockDefinition
import com.algorithmlx.ecr.api.assembled.AssemblyResult
import com.algorithmlx.ecr.api.registries.ECRegistries
import com.algorithmlx.ecr.common.assembled.AssembledMultiblockRuntime
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.Item
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level

class Hammer(properties: Properties) : Item(properties) {
    override fun useOn(context: UseOnContext): InteractionResult {
        val pos = context.clickedPos
        val level = context.level
        val facing = context.horizontalDirection
        val definition = findMatchingDefinition(level, pos, facing)
            ?: return super.useOn(context)

        if (level.isClientSide) return InteractionResult.SUCCESS

        return when (AssembledMultiblockRuntime.assemble(level, definition, pos, facing)) {
            is AssemblyResult.Success -> InteractionResult.SUCCESS
            is AssemblyResult.Failure -> InteractionResult.FAIL
        }
    }

    private fun findMatchingDefinition(
        level: Level,
        selectedPos: BlockPos,
        facing: Direction
    ): AssembledMultiblockDefinition? = ECRegistries.ASSEMBLED_MULTIBLOCK
        .firstOrNull { definition ->
            definition.controllerCandidates(selectedPos, facing).any { controllerPos ->
                definition.matches(level, controllerPos, facing)
            }
        }
}
