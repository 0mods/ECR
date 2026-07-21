package com.algorithmlx.ecr.common.api.block

import com.algorithmlx.ecr.registry.EBlockCodecRegistry
import com.mojang.serialization.MapCodec
import net.minecraft.core.BlockPos
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.shapes.BooleanOp
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape

open class ClusterBlock(properties: Properties) : Block(properties.noOcclusion().strength(1.5F).requiresCorrectToolForDrops()) {
    override fun codec(): MapCodec<out Block> = EBlockCodecRegistry.clusterBlock

    override fun getShape(s: BlockState, l: BlockGetter, p: BlockPos, c: CollisionContext): VoxelShape = shape

    private val shape by lazy {
        var shape = Shapes.empty()
        shape = Shapes.join(shape, Shapes.box(0.1875, 0.0, 0.1875, 0.8125, 0.2875, 0.8125), BooleanOp.OR)
        shape
    }
}
