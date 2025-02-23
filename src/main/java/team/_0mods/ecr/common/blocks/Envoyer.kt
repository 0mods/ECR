package team._0mods.ecr.common.blocks

import net.minecraft.core.BlockPos
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.BooleanOp
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import team._0mods.ecr.api.block.checkAndOpenMenu
import team._0mods.ecr.api.block.client.LowSizeBreakParticle
import team._0mods.ecr.api.block.prepareDrops
import team._0mods.ecr.api.block.simpleTicker
import team._0mods.ecr.common.api.PropertiedEntityBlock
import team._0mods.ecr.common.blocks.entity.XLikeBlockEntity

class Envoyer(properties: Properties) : PropertiedEntityBlock(properties), LowSizeBreakParticle {
    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity = XLikeBlockEntity.Envoyer(pos, state)

    override fun <T : BlockEntity?> getTicker(
        level: Level,
        state: BlockState,
        blockEntityType: BlockEntityType<T>
    ): BlockEntityTicker<T> = simpleTicker<T, XLikeBlockEntity.Envoyer>(XLikeBlockEntity.Envoyer::onTick)

    override fun use(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hand: InteractionHand,
        hit: BlockHitResult
    ): InteractionResult = checkAndOpenMenu<XLikeBlockEntity.Envoyer>(player, level, pos)

    override fun onRemove(state: BlockState, level: Level, pos: BlockPos, newState: BlockState, isMoving: Boolean) {
        prepareDrops<XLikeBlockEntity.Envoyer>(state, level, pos, newState)

        super.onRemove(state, level, pos, newState, isMoving)
    }

    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape = shape

    private val shape by lazy {
        var shape = Shapes.empty()

        shape = Shapes.join(shape, Shapes.box(0.125, 0.0625, 0.125, 0.875, 0.375, 0.875), BooleanOp.OR)
        shape = Shapes.join(shape, Shapes.box(0.4374375, 0.310875, 0.06196875, 0.5625625, 0.686, 0.18709375), BooleanOp.OR)
        shape = Shapes.join(shape, Shapes.box(0.4374375, 0.3124375, 0.81340625, 0.5625625, 0.6875625, 0.93853125), BooleanOp.OR)
        shape = Shapes.join(shape, Shapes.box(0.81384375, 0.3124375, 0.4374375, 0.93896875, 0.6875625, 0.5625625), BooleanOp.OR)
        shape = Shapes.join(shape, Shapes.box(0.06196875, 0.3124375, 0.4374375, 0.18709375, 0.6875625, 0.5625625), BooleanOp.OR)
        shape = Shapes.join(shape, Shapes.box(0.15625, 0.375, 0.15625, 0.84375, 0.4375, 0.21875), BooleanOp.OR)
        shape = Shapes.join(shape, Shapes.box(0.15625, 0.375, 0.78125, 0.84375, 0.4375, 0.84375), BooleanOp.OR)
        shape = Shapes.join(shape, Shapes.box(0.15625, 0.375, 0.21875, 0.21875, 0.4375, 0.78125), BooleanOp.OR)
        shape = Shapes.join(shape, Shapes.box(0.78125, 0.375, 0.21875, 0.84375, 0.4375, 0.78125), BooleanOp.OR)
        shape = Shapes.join(shape, Shapes.box(0.15625, 0.5625, 0.15625, 0.84375, 0.625, 0.84375), BooleanOp.OR)
        shape = Shapes.join(shape, Shapes.box(0.1936875, 0.5999375, 0.19525, 0.8063125, 0.7563125, 0.80475), BooleanOp.OR)
        shape = Shapes.join(shape, Shapes.box(0.15625, 0.75, 0.15625, 0.84375, 0.8125, 0.84375), BooleanOp.OR)
        shape = Shapes.join(shape, Shapes.box(0.15625, 0.0, 0.15625, 0.84375, 0.0625, 0.84375), BooleanOp.OR)

        shape
    }
}