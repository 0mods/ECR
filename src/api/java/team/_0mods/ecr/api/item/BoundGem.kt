package team._0mods.ecr.api.item

import net.minecraft.core.BlockPos
import net.minecraft.world.item.ItemStack
import org.jetbrains.annotations.ApiStatus

interface BoundGem {
    val world: String? get() = null

    val dimensionalBounds: Boolean get() = true

    //use a NOT reversed list! I.e., if you are uses 1000, 100, .., 1, the system will count 1, .., 100, 1000, which is wrong!
    val transferStrength: Array<Int> get() = arrayOf(1, 10, 50, 100, 1000)

    // TODO it is not work.
    @get:ApiStatus.Experimental
    @get:ApiStatus.NonExtendable
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