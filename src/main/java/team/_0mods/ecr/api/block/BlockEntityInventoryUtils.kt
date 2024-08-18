package team._0mods.ecr.api.block

import net.minecraft.core.BlockPos
import net.minecraft.core.NonNullList
import net.minecraft.world.Containers
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraftforge.common.capabilities.ForgeCapabilities

fun BlockEntity.dropForgeContents(level: Level, pos: BlockPos) {
    this.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent {
        val nnl = NonNullList.withSize(it.slots, ItemStack.EMPTY)
        for (i in 0 ..< it.slots)
            nnl[i] = it.getStackInSlot(i)

        Containers.dropContents(level, pos, nnl)
    }
}

inline fun <reified T: BlockEntity> prepareDrops(state: BlockState, level: Level, pos: BlockPos, newState: BlockState) {
    if (state.block != newState.block) {
        val be = level.getBlockEntity(pos)
        if (be is T) {
            be.dropForgeContents(level, pos)
        }
    }
}
