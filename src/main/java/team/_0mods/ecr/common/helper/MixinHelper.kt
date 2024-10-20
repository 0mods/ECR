@file:JvmName("MixinHelper")
package team._0mods.ecr.common.helper

import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.core.particles.SimpleParticleType
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
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

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

        Items.MAGMA_CREAM -> makeStructureCraft(stack, BlockTags.INFINIBURN_NETHER, ECRegistry.flameCluster.get(), level, center, ECRMultiblocks.flameCrystal.get(), timer, ParticleTypes.FALLING_LAVA)

        Items.CLAY_BALL -> makeStructureCraft(stack, BlockTags.ICE, ECRegistry.waterCluster.get(), level, center, ECRMultiblocks.waterCrystal.get(), timer, ParticleTypes.FALLING_WATER)

        Items.SLIME_BALL -> makeStructureCraft(stack, Blocks.MOSS_BLOCK, ECRegistry.earthCluster.get(), level, center, ECRMultiblocks.earthCrystal.get(), timer, ParticleTypes.WAX_ON)

        // TODO("Deprecated! In 1.21 it will changed to Wind Charge")
        Items.GUNPOWDER -> makeStructureCraft(stack, Blocks.PURPUR_BLOCK, ECRegistry.airCluster.get(), level, center, ECRMultiblocks.airCrystal.get(), timer, ParticleTypes.DRIPPING_OBSIDIAN_TEAR)
    }
}

private fun makeStructureCraft(stack: ItemStack, center: TagKey<Block>, result: Block, level: Level, pos: BlockPos, structure: Multiblock, timer: IntArray, pt: SimpleParticleType) {
    if (!level.getBlockState(pos).`is`(center)) return
    makeStructureCraft(stack, result, level, pos, structure, timer, pt)
}

private fun makeStructureCraft(stack: ItemStack, center: Block, result: Block, level: Level, pos: BlockPos, structure: Multiblock, timer: IntArray, pt: SimpleParticleType) {
    if (!level.getBlockState(pos).`is`(center)) return
    makeStructureCraft(stack, result, level, pos, structure, timer, pt)
}

private fun makeStructureCraft(stack: ItemStack, result: Block, level: Level, pos: BlockPos, structure: Multiblock, timer: IntArray, pt: SimpleParticleType) {
    if (!level.getBlockState(pos.above()).`is`(Blocks.AIR)) return
    if (!structure.isValid(level, pos)) {
        timer[0] = 0
        return
    }

    addSpawnParticles(pt, level, pos.above(), 0.0, 0.0, 0.0, 0.5, 0.5, 0.5)

    timer[0] = timer[0] + 1

    if (timer[0] < 20) return

    timer[0] = 0
    stack.shrink(1)

    if (level.random.nextInt(5) < 3) return

    addSpawnParticles(ParticleTypes.POOF, level, pos.above(), 2.0, 2.0, 2.0, 5.5, 5.5, 5.5)
    level.setBlock(pos.above(), result.defaultBlockState(), 3)
}

private fun addSpawnParticles(type: SimpleParticleType, level: Level, pos: BlockPos, minX: Double, minY: Double, minZ: Double, maxX: Double, maxY: Double, maxZ: Double) {
    if (!level.isClientSide) return
    val minXMth = min(1.0, maxX - minX)
    val minYMth = min(1.0, maxY - minY)
    val minZMth = min(1.0, maxZ - minZ)
    val i = max(2.0, ceil(minXMth / 0.25)).roundToInt()
    val j = max(2.0, ceil(minYMth / 0.25)).roundToInt()
    val k = max(2.0, ceil(minZMth / 0.25)).roundToInt()
    for (l in 0 ..< i) {
        for (i1 in 0 ..< j) {
            for (j1 in 0 ..< k) {
                val d1 = (l + 0.5) / i
                val d2 = (i1 + 0.5) / j
                val d3 = (j1 + 0.5) / k
                val d4 = d1 * minXMth + minX
                val d5 = d2 * minYMth + minY
                val d6 = d3 * minZMth + minZ
                level.addParticle(type, d4 + pos.x, d5 + pos.y, d6 + pos.z, d4 - 0.5, d5 - 0.5, d6 - 0.5)
            }
        }
    }
}