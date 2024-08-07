package team._0mods.ecr.common.blocks

import net.minecraft.core.BlockPos
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.shapes.BooleanOp
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape

class CrystalBlock(properties: Properties) : Block(properties) {
    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape =
        makeShape()

    private fun makeShape(): VoxelShape {
        var shape = Shapes.empty()
        shape = Shapes.join(shape, Shapes.box(0.3125, 0.0, 0.3125, 0.6875, 0.0625, 0.6875), BooleanOp.OR)
        shape = Shapes.join(shape, Shapes.box(0.125, 0.125, 0.125, 0.1875, 0.1875, 0.875), BooleanOp.OR)
        shape = Shapes.join(shape, Shapes.box(0.8125, 0.125, 0.125, 0.875, 0.1875, 0.875), BooleanOp.OR)
        shape = Shapes.join(shape, Shapes.box(0.1875, 0.125, 0.8125, 0.8125, 0.1875, 0.875), BooleanOp.OR)
        shape = Shapes.join(shape, Shapes.box(0.1875, 0.125, 0.125, 0.8125, 0.1875, 0.1875), BooleanOp.OR)
        shape = Shapes.join(shape, Shapes.box(0.6875, 0.0625, 0.3125, 0.8125, 0.125, 0.6875), BooleanOp.OR)
        shape = Shapes.join(shape, Shapes.box(0.1875, 0.0625, 0.1875, 0.8125, 0.125, 0.3125), BooleanOp.OR)
        shape = Shapes.join(shape, Shapes.box(0.1875, 0.0625, 0.6875, 0.8125, 0.125, 0.8125), BooleanOp.OR)
        shape = Shapes.join(shape, Shapes.box(0.1875, 0.0625, 0.3125, 0.3125, 0.125, 0.6875), BooleanOp.OR)
        shape = Shapes.join(shape, Shapes.box(0.1875, 0.1875, 0.6875, 0.8125, 0.25, 0.8125), BooleanOp.OR)
        shape = Shapes.join(shape, Shapes.box(0.1875, 0.1875, 0.3125, 0.3125, 0.25, 0.6875), BooleanOp.OR)
        shape = Shapes.join(shape, Shapes.box(0.1875, 0.1875, 0.1875, 0.8125, 0.25, 0.3125), BooleanOp.OR)
        shape = Shapes.join(shape, Shapes.box(0.6875, 0.1875, 0.3125, 0.8125, 0.25, 0.6875), BooleanOp.OR)
        shape = Shapes.join(shape, Shapes.box(0.1875, 0.875, 0.6875, 0.8125, 0.9375, 0.8125), BooleanOp.OR)
        shape = Shapes.join(shape, Shapes.box(0.6875, 0.875, 0.3125, 0.8125, 0.9375, 0.6875), BooleanOp.OR)
        shape = Shapes.join(shape, Shapes.box(0.1875, 0.875, 0.1875, 0.8125, 0.9375, 0.3125), BooleanOp.OR)
        shape = Shapes.join(shape, Shapes.box(0.1875, 0.875, 0.3125, 0.3125, 0.9375, 0.6875), BooleanOp.OR)
        shape = Shapes.join(shape, Shapes.box(0.1875, 0.8125, 0.125, 0.8125, 0.875, 0.1875), BooleanOp.OR)
        shape = Shapes.join(shape, Shapes.box(0.1875, 0.8125, 0.8125, 0.8125, 0.875, 0.875), BooleanOp.OR)
        shape = Shapes.join(shape, Shapes.box(0.125, 0.8125, 0.125, 0.1875, 0.875, 0.875), BooleanOp.OR)
        shape = Shapes.join(shape, Shapes.box(0.8125, 0.8125, 0.125, 0.875, 0.875, 0.875), BooleanOp.OR)
        shape = Shapes.join(shape, Shapes.box(0.3125, 0.9375, 0.3125, 0.6875, 1.0, 0.6875), BooleanOp.OR)
        shape = Shapes.join(shape, Shapes.box(0.1875, 0.75, 0.6875, 0.8125, 0.8125, 0.8125), BooleanOp.OR)
        shape = Shapes.join(shape, Shapes.box(0.1875, 0.75, 0.3125, 0.3125, 0.8125, 0.6875), BooleanOp.OR)
        shape = Shapes.join(shape, Shapes.box(0.1875, 0.75, 0.1875, 0.8125, 0.8125, 0.3125), BooleanOp.OR)
        shape = Shapes.join(shape, Shapes.box(0.6875, 0.75, 0.3125, 0.8125, 0.8125, 0.6875), BooleanOp.OR)
        shape = Shapes.join(shape, Shapes.box(0.3125, 0.1875, 0.3125, 0.6875, 0.8125, 0.6875), BooleanOp.OR)

        return shape
    }
}