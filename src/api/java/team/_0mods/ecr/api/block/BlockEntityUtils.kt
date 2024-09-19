package team._0mods.ecr.api.block

import net.minecraft.core.BlockPos
import net.minecraft.core.NonNullList
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.Containers
import net.minecraft.world.InteractionResult
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.state.BlockState
import net.minecraftforge.common.capabilities.ForgeCapabilities
import net.minecraftforge.network.NetworkHooks

fun BlockEntity?.dropForgeContents(level: Level, pos: BlockPos) {
    this?.getCapability(ForgeCapabilities.ITEM_HANDLER)?.ifPresent {
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

inline fun <reified T> checkAndOpenMenu(player: Player, level: Level, blockPos: BlockPos): InteractionResult where T: BlockEntity, T: MenuProvider {
    if (!level.isClientSide) {
        val be = level.getBlockEntity(blockPos)
        if (be != null && be is T) {
            NetworkHooks.openScreen(player as ServerPlayer, be, be.blockPos)
        } else if (be != null) {
            throw IllegalStateException("Can not open any block entity that is not instanceof {T::class.java}")
        } else return InteractionResult.FAIL
    }

    return InteractionResult.SUCCESS
}

// Don't change T value, use default in "getTicker". V - any block entity.
inline fun <T: BlockEntity?, reified V: BlockEntity> simpleTicker(crossinline onTick: (Level, BlockPos, BlockState, V) -> Unit) =
    BlockEntityTicker<T> { level, blockPos, blockState, entity -> onTick(level, blockPos, blockState, entity as V) }
