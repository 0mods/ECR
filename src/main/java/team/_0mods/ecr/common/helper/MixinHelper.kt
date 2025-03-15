@file:JvmName("MixinHelper")
package team._0mods.ecr.common.helper

import net.minecraft.core.BlockPos
import net.minecraft.tags.BlockTags
import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.phys.Vec3
import ru.hollowhorizon.hc.common.network.sendAllInDimension
import team._0mods.ecr.common.init.registry.ECRMultiblocks
import team._0mods.ecr.common.init.registry.ECRegistry
import team._0mods.ecr.network.FinishCraftParticle

fun makeIntArray(value: Int = 0) = intArrayOf(value)

fun checkCraft(stack: ItemStack, pos: Vec3, level: Level, timer: IntArray) {
    val center = BlockPos.containing(pos).below()

    val container = SimpleContainer(1).apply {
        this.setItem(0, stack)
    }

    val recipe = level.recipeManager.getRecipeFor(ECRegistry.structureRecipe, container, level)

    if (!recipe.isPresent) {
        container.clearContent()
        return
    }

    val r = recipe.get()

    val s = level.getBlockState(center)
    val isAtCenter = when (r.multiblock) {
        ECRMultiblocks.soulStone -> s.`is`(Blocks.EMERALD_BLOCK)
        ECRMultiblocks.flameCrystal -> s.`is`(BlockTags.INFINIBURN_NETHER)
        ECRMultiblocks.waterCrystal -> s.`is`(BlockTags.ICE)
        ECRMultiblocks.earthCrystal -> s.`is`(Blocks.MOSS_BLOCK)
        ECRMultiblocks.airCrystal -> s.`is`(Blocks.PURPUR_BLOCK)
        else -> true
    }

    if (!isAtCenter) return

    if (!r.multiblock.isValid(level, center)) {
        timer[0] = 0
        return
    }

    val b = r.blockForPlace
    if (b != null && !level.getBlockState(center.above()).`is`(Blocks.AIR)) return

    timer[0] = timer[0] + 1
    if (timer[0] < r.time) return

    timer[0] = 0
    stack.shrink(r.ingredients[0].items[0].count)

    if ((r.maxChance == 0 && r.minChance == 0) || level.random.nextInt(r.maxChance) >= r.minChance) {
        if (!r.result.isEmpty) {
            val item = ItemEntity(level, pos.x, pos.y, pos.z, r.result).apply { this.setNoPickUpDelay() }
            level.addFreshEntity(item)
        }

        if (b != null) {
            level.setBlock(center.above(), b.defaultBlockState(), Block.UPDATE_NEIGHBORS or Block.UPDATE_CLIENTS or Block.UPDATE_SUPPRESS_DROPS)
        }

        FinishCraftParticle(center.above().x + 0.5, center.above().y + 0.5, center.above().z + 0.5, 80).sendAllInDimension(level)
    }
}
