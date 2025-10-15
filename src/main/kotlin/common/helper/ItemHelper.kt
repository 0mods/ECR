package team._0mods.ecr.common.helper

import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.ItemLike

fun ItemLike.ofStack(count: Int = 1) = ItemStack(this, count)