package team._0mods.ecr.api.item

import net.minecraft.core.BlockPos
import net.minecraft.world.item.ItemStack

interface BoundGem {
    val world: String? get() = null

    val dimensionalBounds: Boolean get() = true

    val boundRadius: Double get() = 16.0

    fun getBoundPos(stack: ItemStack): BlockPos? {
        if (stack.item !is BoundGem) return null
        val tag = stack.orCreateTag
        if (!tag.contains("BoundGemX") && !tag.contains("BoundGemY") && !tag.contains("BoundGemZ")) return null
        return BlockPos(tag.getInt("BoundGemX"), tag.getInt("BoundGemY"), tag.getInt("BoundGemZ"))
    }

    fun setBoundPos(stack: ItemStack, blockPos: BlockPos?) {
        if (stack.item !is BoundGem) return
        val tag = stack.orCreateTag

        if (blockPos == null) {
            stack.tag = null
        } else {
            tag.putInt("BoundGemX", blockPos.x)
            tag.putInt("BoundGemY", blockPos.y)
            tag.putInt("BoundGemZ", blockPos.z)
        }
    }

    fun getBoundedWorld(stack: ItemStack): String? {
        if (stack.item !is BoundGem) return null
        val tag = stack.orCreateTag
        if (!(stack.item as BoundGem).dimensionalBounds) {
            if (tag.contains("BoundDimension")) tag.remove("BoundDimension")
            return null
        }
        if (!tag.contains("BoundDimension")) return null
        return tag.getString(tag.getString("BoundDimension"))
    }

    fun setBoundedWorld(stack: ItemStack, world: String?) {
        if (stack.item !is BoundGem) return
        val tag = stack.orCreateTag
        if (!(stack.item as BoundGem).dimensionalBounds) {
            if (tag.contains("BoundDimension")) tag.remove("BoundDimension")
            return
        }
        if (world == null) {
            tag.remove("BoundDimension")
        } else {
            tag.putString("BoundDimension", world)
        }
    }
}