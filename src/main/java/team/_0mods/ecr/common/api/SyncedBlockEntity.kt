package team._0mods.ecr.common.api

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import ru.hollowhorizon.hc.common.objects.blocks.HollowBlockEntity

@Deprecated("Change to HollowBlockEntity!", replaceWith = ReplaceWith("HollowBlockEntity", "ru.hollowhorizon.hc.common.objects.blocks.HollowBlockEntity"))
open class SyncedBlockEntity(type: BlockEntityType<*>, pos: BlockPos, state: BlockState): HollowBlockEntity(type, pos, state)