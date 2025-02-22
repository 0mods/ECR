package team._0mods.ecr.api.block.entity

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import ru.hollowhorizon.hc.common.registry.HollowRegistry

fun <T: BlockEntity> HollowRegistry.simpleBlockEntityType(blockEntity: (BlockPos, BlockState) -> T, vararg blocks: Block): BlockEntityType<T> =
    BlockEntityType.Builder.of(blockEntity, *blocks).build(promise())
