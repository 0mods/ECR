package team._0mods.ecr.api.block

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.Container
import net.minecraft.world.Containers
import net.minecraft.world.InteractionResult
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.state.BlockState
//? if forge
/*import net.minecraftforge.network.NetworkHooks*/

fun Container.dropContents(level: Level, pos: BlockPos) {
    Containers.dropContents(level, pos, this)
}

inline fun <reified T: BlockEntity> prepareDrops(container: (T) -> Container, state: BlockState, level: Level, pos: BlockPos, newState: BlockState) {
    if (state.block != newState.block) {
        val be = level.getBlockEntity(pos)
        if (be is T) {
            container(be).dropContents(level, pos)
        }
    }
}

inline fun <reified T> checkAndOpenMenu(player: Player, level: Level, blockPos: BlockPos): InteractionResult where T: BlockEntity, T: MenuProvider {
    if (!level.isClientSide) {
        val be = level.getBlockEntity(blockPos)
        if (be != null && be is T) {
            player as ServerPlayer
            //? if forge {
            /*NetworkHooks.openScreen(player, be, be.blockPos)
            *///?} else {
            player.openMenu(be)
            //?}
        } else if (be != null) {
            throw IllegalStateException("Can not open any block entity that is not instanceof ${T::class.java}")
        } else return InteractionResult.FAIL
    }

    return InteractionResult.SUCCESS
}

// Don't change T value, use default in "getTicker". V - any block entity.
inline fun <T: BlockEntity?, reified V: BlockEntity> simpleTicker(crossinline onTick: (Level, BlockPos, BlockState, V) -> Unit) =
    BlockEntityTicker<T> { level, blockPos, blockState, entity -> onTick(level, blockPos, blockState, entity as V) }
