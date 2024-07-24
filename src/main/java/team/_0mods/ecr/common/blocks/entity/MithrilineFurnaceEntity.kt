package team._0mods.ecr.common.blocks.entity

import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import team._0mods.ecr.common.init.registry.ECRegistry

class MithrilineFurnaceEntity(pos: BlockPos, blockState: BlockState) :
    BlockEntity(ECRegistry.mithrilineFurnace.second, pos, blockState) {
    companion object {
        @JvmStatic
        fun onServerTick(level: Level, pos: BlockPos, state: BlockState, be: MithrilineFurnaceEntity) {}

        @JvmStatic
        fun onClientTick(level: Level, pos: BlockPos, state: BlockState, be: MithrilineFurnaceEntity) {}
    }
}