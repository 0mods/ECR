package team._0mods.ecr.common.blocks.base

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BooleanProperty

class ConnectedTextureBlock(properties: Properties) : Block(properties) {
    companion object {
        @JvmField val CONNECTED_NORTH: BooleanProperty = BooleanProperty.create("north_connect")
        @JvmField val CONNECTED_SOUTH: BooleanProperty = BooleanProperty.create("south_connect")
        @JvmField val CONNECTED_EAST: BooleanProperty = BooleanProperty.create("east_connect")
        @JvmField val CONNECTED_WEST: BooleanProperty = BooleanProperty.create("west_connect")
    }

    init {
        this.registerDefaultState(
            this.stateDefinition.any()
                .setValue(CONNECTED_NORTH, false)
                .setValue(CONNECTED_SOUTH, false)
                .setValue(CONNECTED_EAST, false)
                .setValue(CONNECTED_WEST, false)
        )
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(CONNECTED_NORTH, CONNECTED_SOUTH, CONNECTED_EAST, CONNECTED_WEST)
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState? {
        val level = context.level
        val pos = context.clickedPos
        return this.defaultBlockState().setValue(CONNECTED_NORTH, this.shouldConnectTo(level, pos.north()))
            .setValue(CONNECTED_SOUTH, this.shouldConnectTo(level, pos.south()))
            .setValue(CONNECTED_EAST, this.shouldConnectTo(level, pos.east()))
            .setValue(CONNECTED_WEST, this.shouldConnectTo(level, pos.west()))

    }

    override fun updateShape(
        state: BlockState,
        direction: Direction,
        neighborState: BlockState,
        level: LevelAccessor,
        currentPos: BlockPos,
        neighborPos: BlockPos
    ): BlockState {
        return state.setValue(CONNECTED_NORTH, this.shouldConnectTo(level, currentPos.north()))
            .setValue(CONNECTED_SOUTH, this.shouldConnectTo(level, currentPos.south()))
            .setValue(CONNECTED_EAST, this.shouldConnectTo(level, currentPos.east()))
            .setValue(CONNECTED_WEST, this.shouldConnectTo(level, currentPos.west()))
    }

    private fun shouldConnectTo(accessor: LevelAccessor, pos: BlockPos): Boolean {
        val block = accessor.getBlockState(pos).block
        return block is ConnectedTextureBlock && block == this
    }
}