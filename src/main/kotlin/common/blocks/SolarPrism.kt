package team._0mods.ecr.common.blocks

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.sounds.SoundEvent
import net.minecraft.world.item.DyeColor
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.block.BeaconBeamBlock
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.CrossCollisionBlock
import net.minecraft.world.level.block.SupportType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.material.Fluid
import net.minecraft.world.level.material.FluidState
import net.minecraft.world.level.material.Fluids
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import ru.hollowhorizon.hc.common.objects.blocks.BlockItemProperties
import java.util.*

class SolarPrism(properties: Properties): CrossCollisionBlock(
    8f, 8f, 16f, 16f, 16f,
    properties
), BlockItemProperties, BeaconBeamBlock {
    private val northShape = Shapes.box(0.0, 0.4375, 0.0, 1.0, 0.5625, 0.125)
    private val eastShape = Shapes.box(0.875, 0.4375, 0.0, 1.0, 0.5625, 1.0)
    private val southShape = Shapes.box(0.0, 0.4375, 0.875, 1.0, 0.5625, 1.0)
    private val westShape = Shapes.box(0.0, 0.4375, 0.0, 0.125, 0.5625, 1.0)
    private val sideShape = Shapes.box(0.0, 0.484375, 0.0, 1.0, 0.515625, 1.0)

    override val properties: Item.Properties = Item.Properties()

    init {
        this.registerDefaultState(
            this.stateDefinition.any().setValue(NORTH, false)
                .setValue(EAST, false)
                .setValue(SOUTH, false)
                .setValue(WEST, false)
        )
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block?, BlockState?>) {
        builder.add(NORTH, EAST, WEST, SOUTH)
    }

    override fun propagatesSkylightDown(state: BlockState, level: BlockGetter, pos: BlockPos): Boolean =
        !isShapeFullBlock(state.getShape(level, pos)) && state.fluidState.isEmpty

    override fun canPlaceLiquid(level: BlockGetter, pos: BlockPos, state: BlockState, fluid: Fluid): Boolean = false

    override fun getFluidState(state: BlockState): FluidState? = Fluids.EMPTY.defaultFluidState()

    override fun placeLiquid(level: LevelAccessor, pos: BlockPos, state: BlockState, fluidState: FluidState): Boolean = false

    override fun pickupBlock(level: LevelAccessor, pos: BlockPos, state: BlockState): ItemStack? = ItemStack.EMPTY

    override fun getPickupSound(): Optional<SoundEvent> = Optional.empty()

    override fun updateShape(state: BlockState, direction: Direction, neighborState: BlockState, level: LevelAccessor, pos: BlockPos, neighborPos: BlockPos): BlockState {
        if (direction.axis.isHorizontal) {
            val newValue = attach(neighborState)
            return state.setValue(PROPERTY_BY_DIRECTION[direction], newValue)
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos)
    }

    override fun skipRendering(state: BlockState, adjacentState: BlockState, direction: Direction): Boolean {
        if (adjacentState.`is`(this)) {
            if (!direction.axis.isHorizontal) return true

            if (state.getValue(PROPERTY_BY_DIRECTION.get(direction)) && adjacentState.getValue(PROPERTY_BY_DIRECTION.get(direction.opposite))) return true
        }

        return super.skipRendering(state, adjacentState, direction)
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState {
        val blockGetter: BlockGetter = context.level
        val blockPos = context.clickedPos
        val north = blockPos.north()
        val south = blockPos.south()
        val west = blockPos.west()
        val east = blockPos.east()
        val northState = blockGetter.getBlockState(north)
        val southState = blockGetter.getBlockState(south)
        val westState = blockGetter.getBlockState(west)
        val eastState = blockGetter.getBlockState(east)

        return this.defaultBlockState()
            .setValue(NORTH, attach(northState))
            .setValue(SOUTH, attach(southState))
            .setValue(WEST, attach(westState))
            .setValue(EAST, attach(eastState))
    }

    override fun getColor(): DyeColor = DyeColor.YELLOW

    override fun getShape(
        state: BlockState,
        level: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape {
        var shape = sideShape

        shape = when {
            !state.getValue(NORTH) -> Shapes.or(shape, northShape)
            !state.getValue(EAST) -> Shapes.or(shape, eastShape)
            !state.getValue(SOUTH) -> Shapes.or(shape, southShape)
            !state.getValue(WEST) -> Shapes.or(shape, westShape)
            else -> Shapes.empty()
        }

        return shape
    }

    override fun getCollisionShape(
        state: BlockState,
        level: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape {
        return getShape(state, level, pos, context)
    }

    companion object {
        @JvmStatic
        fun attach(state: BlockState) = state.block is SolarPrism
    }
}
