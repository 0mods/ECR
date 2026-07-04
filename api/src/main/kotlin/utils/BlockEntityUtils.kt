package com.algorithmlx.ecr.api.utils

import net.minecraft.core.BlockPos
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.Container
import net.minecraft.world.Containers
import net.minecraft.world.InteractionResult
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState

fun <T: BlockEntity> simpleBlockEntity(blockEntity: (BlockPos, BlockState) -> T, vararg blocks: Block): BlockEntityType<T> =
    BlockEntityType({ pos, state -> blockEntity(pos, state) }, blocks.toSet())

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
        if (be is T) {
            player as ServerPlayer
            player.openMenuScreen(be, level, be.blockPos)
        } else if (be != null) {
            throw IllegalStateException("Can not open any block entity that is not instanceof ${T::class.java}")
        } else return InteractionResult.FAIL
    }

    return InteractionResult.SUCCESS
}

inline fun <T: BlockEntity, reified V: BlockEntity> simpleTicker(crossinline onTick: (Level, BlockPos, BlockState, V) -> Unit) =
    BlockEntityTicker<T> { level, pos, state, entity -> onTick(level, pos, state, entity as V) }

// INITIALIZED ON PLATFORM
lateinit var openMenuScreenInternal: (player: Player, provider: MenuProvider, level: Level, pos: BlockPos) -> Unit
fun Player.openMenuScreen(provider: MenuProvider, level: Level, pos: BlockPos) = openMenuScreenInternal(this, provider, level, pos)
