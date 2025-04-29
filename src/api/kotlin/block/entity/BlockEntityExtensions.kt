package team._0mods.ecr.api.block.entity

import net.minecraft.core.BlockPos
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState

interface BlockEntityExtensions<T: BlockEntity> {
    fun onPlace(level: Level, state: BlockState, oldState: BlockState, isMoving: Boolean)

    fun onRemove(level: Level, state: BlockState, oldState: BlockState, isMoving: Boolean)

    fun onPlacedBy(level: Level, state: BlockState, placer: LivingEntity?, stack: ItemStack)

    fun <K: T> onTick(level: Level, pos: BlockPos, state: BlockState, blockEntity: K) {}

    fun <K: T> onClientTick(level: Level, pos: BlockPos, state: BlockState, blockEntity: K) {}
}
