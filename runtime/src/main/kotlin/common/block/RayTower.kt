package com.algorithmlx.ecr.common.block

import com.algorithmlx.ecr.api.assembled.AssembledMultiblockControllerBlock
import com.algorithmlx.ecr.api.assembled.AssembledMultiblocks
import com.algorithmlx.ecr.api.block.FullBlockParticles
import com.algorithmlx.ecr.api.registries.ECRegistries
import com.algorithmlx.ecr.api.utils.checkAndOpenMenu
import com.algorithmlx.ecr.api.utils.ecRL
import com.algorithmlx.ecr.api.utils.simpleTicker
import com.algorithmlx.ecr.common.block.entity.RayTowerEntity
import com.algorithmlx.ecr.common.init.ECRModIDs
import com.algorithmlx.ecr.registry.BlockCodecRegistry
import com.mojang.serialization.MapCodec
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Explosion
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import java.util.function.BiConsumer

class RayTower(properties: Properties): Block(properties), EntityBlock, AssembledMultiblockControllerBlock, FullBlockParticles {
    init {
        registerDefaultState(stateDefinition.any().setValue(ASSEMBLED, false))
    }

    override fun codec(): MapCodec<out Block> = BlockCodecRegistry.instance.rayTower

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(ASSEMBLED)
    }

    override fun assembledState(originalState: BlockState, facing: Direction): BlockState =
        originalState.setValue(ASSEMBLED, true)

    override fun newBlockEntity(
        worldPosition: BlockPos,
        blockState: BlockState
    ): BlockEntity = RayTowerEntity(worldPosition, blockState)

    override fun <T : BlockEntity> getTicker(
        level: Level,
        blockState: BlockState,
        type: BlockEntityType<T>
    ): BlockEntityTicker<T> = simpleTicker<T, RayTowerEntity> { level, blockPos, _, blockEntity ->
        RayTowerEntity.onTick(level, blockPos, blockEntity)
    }

    override fun useWithoutItem(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hitResult: BlockHitResult
    ): InteractionResult {
        if (!state.getValue(ASSEMBLED)) return InteractionResult.FAIL
        return checkAndOpenMenu<RayTowerEntity>(player, level, pos)
    }

    override fun getRenderShape(state: BlockState): RenderShape =
        if (state.getValue(ASSEMBLED) && hasFormedModel()) RenderShape.INVISIBLE else super.getRenderShape(state)

    override fun getShape(
        state: BlockState,
        level: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape = if (state.getValue(ASSEMBLED)) {
        AssembledMultiblocks.formedSelectionShape(level, pos) ?: Shapes.block()
    } else {
        super.getShape(state, level, pos, context)
    }

    override fun getCollisionShape(
        state: BlockState,
        level: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape = if (state.getValue(ASSEMBLED)) {
        AssembledMultiblocks.formedPartShape(level, pos) ?: Shapes.block()
    } else {
        super.getCollisionShape(state, level, pos, context)
    }

    override fun playerWillDestroy(
        level: Level,
        pos: BlockPos,
        state: BlockState,
        player: Player
    ): BlockState {
        if (!state.getValue(ASSEMBLED)) return super.playerWillDestroy(level, pos, state, player)
        val original = AssembledMultiblocks.disassemble(level, pos)
            ?: return super.playerWillDestroy(level, pos, state, player)
        return original.state.block.playerWillDestroy(level, pos, original.state, player)
    }

    override fun onExplosionHit(
        state: BlockState,
        level: ServerLevel,
        pos: BlockPos,
        explosion: Explosion,
        dropConsumer: BiConsumer<ItemStack, BlockPos>
    ) {
        if (state.getValue(ASSEMBLED)) {
            val restored = AssembledMultiblocks.disassemble(level, pos)?.state
            if (restored != null) {
                restored.onExplosionHit(level, pos, explosion, dropConsumer)
                return
            }
        }
        super.onExplosionHit(state, level, pos, explosion, dropConsumer)
    }

    private fun hasFormedModel(): Boolean = ECRegistries.ASSEMBLED_MULTIBLOCK
        .getOptional(ECRModIDs.RAY_TOWER.ecRL)
        .orElse(null)
        ?.formedModel != null

    companion object {
        @JvmField
        val ASSEMBLED: BooleanProperty = BooleanProperty.create("assembled")
    }
}
