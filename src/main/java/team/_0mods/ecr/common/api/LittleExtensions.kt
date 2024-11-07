package team._0mods.ecr.common.api

import net.minecraft.core.BlockPos
import net.minecraft.world.inventory.ContainerLevelAccess
import net.minecraft.world.level.Level

fun ContainerLevelAccess(level: Level, blockPos: BlockPos): ContainerLevelAccess = ContainerLevelAccess.create(level, blockPos)