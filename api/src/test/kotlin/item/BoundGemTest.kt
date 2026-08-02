package com.algorithmlx.ecr.api.item

import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BoundGemTest {
    private val gem = object : BoundGem {
        override val boundRadius = 5.0

        override fun getBoundPos(stack: ItemStack): BlockPos? = null
        override fun setBoundPos(stack: ItemStack, blockPos: BlockPos?) = Unit
        override fun getWorld(stack: ItemStack): ResourceKey<Level>? = null
        override fun setWorld(stack: ItemStack, world: ResourceKey<Level>?) = Unit
    }

    @Test
    fun `checks an inclusive spherical bound radius`() {
        val origin = BlockPos(10, 20, 30)

        assertTrue(gem.isWithinBoundRadius(origin, BlockPos(13, 24, 30)))
        assertFalse(gem.isWithinBoundRadius(origin, BlockPos(13, 24, 31)))
    }
}
