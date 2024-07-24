package team._0mods.ecr.common.blocks

import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import team._0mods.ecr.common.blocks.entity.MithrilineFurnaceEntity

class MithrilineFurnace(properties: Properties) : BaseEntityBlock(properties) {
    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity = MithrilineFurnaceEntity(pos, state)

    override fun <T : BlockEntity> getTicker(
        level: Level,
        state: BlockState,
        blockEntityType: BlockEntityType<T>
    ): BlockEntityTicker<T> {
        return if (level.isClientSide)
            BlockEntityTicker<T> { l, bp, s, e -> MithrilineFurnaceEntity.onClientTick(l, bp, s, e as MithrilineFurnaceEntity) }
        else BlockEntityTicker<T> { l, bp, s, e -> MithrilineFurnaceEntity.onServerTick(l, bp, s, e as MithrilineFurnaceEntity) }
    }
}