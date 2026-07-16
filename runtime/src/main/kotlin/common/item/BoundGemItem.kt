package com.algorithmlx.ecr.common.item

import com.algorithmlx.ecr.api.item.BoundGem
import com.algorithmlx.ecr.common.api.BoundGemHelper
import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level

class BoundGemItem(properties: Properties): Item(properties), BoundGem {
    override val dimensionalBounds: Boolean = false

    override fun getBoundPos(stack: ItemStack): BlockPos? = BoundGemHelper.getBoundPos(stack)
    override fun setBoundPos(stack: ItemStack, blockPos: BlockPos?) = BoundGemHelper.setBoundPos(stack, blockPos)
    override fun getWorld(stack: ItemStack): ResourceKey<Level>? = BoundGemHelper.getWorld(stack)
    override fun setWorld(stack: ItemStack, world: ResourceKey<Level>?) = BoundGemHelper.setWorld(stack, world)
}
