package team._0mods.ecr.common.blocks

import net.minecraft.core.BlockPos
import net.minecraft.world.item.Item
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.shapes.BooleanOp
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import team._0mods.ecr.common.api.PropertiedBlock

class ClusterBlock(properties: Properties): PropertiedBlock(properties) {
    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape = shape

    private val shape by lazy {
        var shape = Shapes.empty()
        shape = Shapes.join(shape, Shapes.box(0.1875, 0.0, 0.1875, 0.8125, 0.2875, 0.8125), BooleanOp.OR)
        shape
    }

    override val properties: Item.Properties = Item.Properties()
}