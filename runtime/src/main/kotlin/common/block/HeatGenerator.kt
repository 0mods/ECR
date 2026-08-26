package com.algorithmlx.ecr.common.block

import com.algorithmlx.ecr.api.ModId
import com.algorithmlx.ecr.common.block.entity.HeatGeneratorEntity
import com.algorithmlx.ecr.common.init.ECRModIDs
import com.algorithmlx.ecr.registry.BlockCodecRegistry
import com.mojang.serialization.MapCodec
import net.minecraft.core.BlockPos
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.BlockItemStateProperties
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.level.storage.loot.LootParams

class HeatGenerator(
    properties: Properties,
): Block(properties), EntityBlock {
    override fun codec(): MapCodec<out Block> = BlockCodecRegistry.instance.heatGenerator

    init {
        this.registerDefaultState(this.stateDefinition.any().setValue(IS_UPGRADED, false))
    }

    override fun newBlockEntity(
        worldPosition: BlockPos,
        blockState: BlockState,
    ): BlockEntity = HeatGeneratorEntity(worldPosition, blockState)

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState {
        val isUpgraded = context.itemInHand[DataComponents.BLOCK_STATE]?.get(IS_UPGRADED) ?: false
        return this.defaultBlockState().setValue(IS_UPGRADED, isUpgraded)
    }

    override fun setPlacedBy(
        level: Level,
        pos: BlockPos,
        state: BlockState,
        placer: LivingEntity?,
        stack: ItemStack,
    ) {
        super.setPlacedBy(level, pos, state, placer, stack)
        val blockEntity = level.getBlockEntity(pos) as? HeatGeneratorEntity ?: return
        blockEntity.isUpgraded = state.getValue(IS_UPGRADED) || blockEntity.isUpgraded
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(IS_UPGRADED)
    }

    override fun getDrops(
        state: BlockState,
        params: LootParams.Builder,
    ): List<ItemStack> {
        val drops = super.getDrops(state, params)
        drops.firstOrNull { it.`is`(this.asItem()) }?.storeUpgradeState(state.getValue(IS_UPGRADED))
        return drops
    }

    override fun getCloneItemStack(
        level: LevelReader,
        pos: BlockPos,
        state: BlockState,
        includeData: Boolean,
    ): ItemStack = super.getCloneItemStack(level, pos, state, includeData).storeUpgradeState(state.getValue(IS_UPGRADED))

    private fun ItemStack.storeUpgradeState(isUpgraded: Boolean): ItemStack = apply {
        if (isUpgraded) {
            this[DataComponents.BLOCK_STATE] = BlockItemStateProperties.EMPTY.with(IS_UPGRADED, true)
            this[DataComponents.ITEM_NAME] = Component.translatable("block.$ModId.ultra_${ECRModIDs.HEAT_GENERATOR}")
        }
    }

    companion object {
        @JvmField
        val IS_UPGRADED = BooleanProperty.create("upgraded")
    }
}
