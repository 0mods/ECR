@file:JvmName("MixinHelper")
package team._0mods.ecr.common.helper

import net.minecraft.core.BlockPos
import net.minecraft.tags.BlockTags
import net.minecraft.tags.TagKey
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.phys.Vec3
import ru.hollowhorizon.hc.common.multiblock.Multiblock
import team._0mods.ecr.common.init.registry.ECRMultiblocks
import team._0mods.ecr.common.init.registry.ECRegistry
import team._0mods.ecr.common.particle.ECParticleOptions
import java.awt.Color

fun makeIntArray(value: Int = 0) = intArrayOf(value)

fun checkCraft(stack: ItemStack, pos: Vec3, level: Level, timer: IntArray) {
    val center = BlockPos(pos).below()
    when (stack.item) {
        Items.EMERALD -> {
            if (!level.getBlockState(center).`is`(Blocks.EMERALD_BLOCK)) return

            if (!ECRMultiblocks.soulStone.get().isValid(level, center)) {
                timer[0] = 0
                return
            }

            level.addParticle(
                ECParticleOptions(Color.GREEN, 0.5f, 40, 0.05f, false, false),
                pos.x, pos.y + 0.5, pos.z, 1.0, 1.0, 1.0
            )

            timer[0] = timer[0] + 1
            if (timer[0] < 40) return

            timer[0] = 0
            stack.shrink(1)

            if (level.random.nextInt(10) > 6) {
                val item = ItemEntity(level, pos.x, pos.y, pos.z, ItemStack(ECRegistry.soulStone.get())).apply {
                    this.setNoPickUpDelay()
                }

                level.addFreshEntity(item)
            }
        }

        Items.MAGMA_CREAM -> makeStructureCraft(stack, BlockTags.INFINIBURN_NETHER, ECRegistry.flameCluster.get(), level, center, ECRMultiblocks.flameCrystal.get(), timer)

        Items.CLAY_BALL -> makeStructureCraft(stack, BlockTags.ICE, ECRegistry.waterCluster.get(), level, center, ECRMultiblocks.waterCrystal.get(), timer)

        Items.SLIME_BALL -> makeStructureCraft(stack, Blocks.MOSS_BLOCK, ECRegistry.earthCluster.get(), level, center, ECRMultiblocks.earthCrystal.get(), timer)

        // TODO("Deprecated! In 1.21 it will changed to Wind Charge")
        Items.GUNPOWDER -> makeStructureCraft(stack, Blocks.PURPUR_BLOCK, ECRegistry.airCluster.get(), level, center, ECRMultiblocks.airCrystal.get(), timer)
    }
}

private fun makeStructureCraft(stack: ItemStack, center: TagKey<Block>, result: Block, level: Level, pos: BlockPos, structure: Multiblock, timer: IntArray) {
    if (!level.getBlockState(pos).`is`(center)) return
    makeStructureCraft(stack, result, level, pos, structure, timer)
}

private fun makeStructureCraft(stack: ItemStack, center: Block, result: Block, level: Level, pos: BlockPos, structure: Multiblock, timer: IntArray) {
    if (!level.getBlockState(pos).`is`(center)) return
    makeStructureCraft(stack, result, level, pos, structure, timer)
}

private fun makeStructureCraft(stack: ItemStack, result: Block, level: Level, pos: BlockPos, structure: Multiblock, timer: IntArray) {
    if (!level.getBlockState(pos.above()).`is`(Blocks.AIR)) return
    if (!structure.isValid(level, pos)) {
        timer[0] = 0
        return
    }

    timer[0] = timer[0] + 1

    if (timer[0] < 20) return

    timer[0] = 0
    stack.shrink(1)

    if (level.random.nextInt(5) < 3) return

    level.setBlock(pos.above(), result.defaultBlockState(), 3)
}