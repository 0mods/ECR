package team._0mods.ecr.api.item

import net.minecraft.core.BlockPos
import net.minecraft.world.item.ItemStack

interface BoundGem {
    val world: String? get() = null

    fun getBlockPos(stack: ItemStack): BlockPos? {
        if (stack.item !is BoundGem) throw UnsupportedOperationException()
        val tag = stack.orCreateTag
        if (!tag.contains("BoundGemX") && !tag.contains("BoundGemY") && !tag.contains("BoundGemZ")) return null
        return BlockPos(tag.getInt("BoundGemX"), tag.getInt("BoundGemY"), tag.getInt("BoundGemZ"))
    }

    fun setBlockPos(stack: ItemStack, blockPos: BlockPos?) {
        if (stack.item !is BoundGem) throw UnsupportedOperationException()
        val tag = stack.orCreateTag

        if (blockPos == null) {
            stack.tag = null
        } else {
            tag.putInt("BoundGemX", blockPos.x)
            tag.putInt("BoundGemY", blockPos.y)
            tag.putInt("BoundGemZ", blockPos.z)
        }
    }
}