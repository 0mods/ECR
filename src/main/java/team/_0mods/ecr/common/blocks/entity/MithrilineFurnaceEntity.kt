package team._0mods.ecr.common.blocks.entity

import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import team._0mods.ecr.common.init.registry.ECMultiblocks
import team._0mods.ecr.common.init.registry.ECRegistry

class MithrilineFurnaceEntity(pos: BlockPos, blockState: BlockState) :
    BlockEntity(ECRegistry.mithrilineFurnace.second, pos, blockState) {
    companion object {
        @JvmStatic
        fun onTick(level: Level, pos: BlockPos, state: BlockState, be: MithrilineFurnaceEntity) {
            be.successfulStructure = ECMultiblocks.mithrilineFurnace.isComplete(level, pos)

            if (be.successfulStructure) {
                be.ticks++
            } else be.ticks = 0
        }
    }

    var successfulStructure = false
    var ticks: Int = 0

    override fun saveAdditional(tag: CompoundTag) {
        super.saveAdditional(tag)
        tag.putBoolean("FullStructure", successfulStructure)
    }

    override fun load(tag: CompoundTag) {
        super.load(tag)

        successfulStructure = tag.getBoolean("Working")
    }
}