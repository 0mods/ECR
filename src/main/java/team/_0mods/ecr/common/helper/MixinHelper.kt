@file:JvmName("MixinHelper")
package team._0mods.ecr.common.helper

import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ParticleTypes
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
import kotlin.math.roundToInt
import kotlin.random.Random

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

        Items.MAGMA_CREAM -> makeStructureCraft(stack, BlockTags.INFINIBURN_NETHER, ECRegistry.flameCluster.get(), level, center, ECRMultiblocks.flameCrystal.get(), timer, Color.ORANGE)

        Items.CLAY_BALL -> makeStructureCraft(stack, BlockTags.ICE, ECRegistry.waterCluster.get(), level, center, ECRMultiblocks.waterCrystal.get(), timer, Color.BLUE)

        Items.SLIME_BALL -> makeStructureCraft(stack, Blocks.MOSS_BLOCK, ECRegistry.earthCluster.get(), level, center, ECRMultiblocks.earthCrystal.get(), timer, Color.GREEN)

        // TODO("Deprecated! In 1.21 it will changed to Wind Charge")
        Items.GUNPOWDER -> makeStructureCraft(stack, Blocks.PURPUR_BLOCK, ECRegistry.airCluster.get(), level, center, ECRMultiblocks.airCrystal.get(), timer, Color.WHITE)
    }
}

private fun makeStructureCraft(stack: ItemStack, center: TagKey<Block>, result: Block, level: Level, pos: BlockPos, structure: Multiblock, timer: IntArray, color: Color) {
    if (!level.getBlockState(pos).`is`(center)) return
    makeStructureCraft(stack, result, level, pos, structure, timer, color)
}

private fun makeStructureCraft(stack: ItemStack, center: Block, result: Block, level: Level, pos: BlockPos, structure: Multiblock, timer: IntArray, color: Color) {
    if (!level.getBlockState(pos).`is`(center)) return
    makeStructureCraft(stack, result, level, pos, structure, timer, color)
}

private fun makeStructureCraft(stack: ItemStack, result: Block, level: Level, pos: BlockPos, structure: Multiblock, timer: IntArray, color: Color) {
    if (!level.getBlockState(pos.above()).`is`(Blocks.AIR)) return
    if (!structure.isValid(level, pos)) {
        timer[0] = 0
        return
    }

    val v = pos.above()
    addSpawnParticles(color, level, Vec3(v.x + 0.0, v.y + 0.5, v.z + 0.0))

    timer[0] = timer[0] + 1

    if (timer[0] < 20) return

    timer[0] = 0
    stack.shrink(1)

    if (level.random.nextInt(5) < 3) return

    level.setBlock(pos.above(), result.defaultBlockState(), Block.UPDATE_NEIGHBORS or Block.UPDATE_CLIENTS or Block.UPDATE_SUPPRESS_DROPS)
    addFinalParticle(level, pos.above().x + 0.0, pos.above().y + 0.5, pos.above().z + 0.0, 80)
}

private fun addSpawnParticles(color: Color, level: Level, pos: Vec3) {
    if (!level.isClientSide) return
    val rand = Random

    val xd = rand.nextDouble(-0.06, 0.06) + (Math.random() * 2.0 - 1.0) * 0.05F
    val yd = rand.nextDouble(-0.0, 0.15) + (Math.random() * 2.0 - 1.0) * 0.05F
    val zd = rand.nextDouble(-0.06, 0.06) + (Math.random() * 2.0 - 1.0) * 0.05F
    val fric = 0.9F
    val grav = -0.1F

    for (i in 0 ..< 15) {
        level.addParticle(
            ECParticleOptions(
                color,
                0.1F * level.random.nextFloat() * level.random.nextFloat() * 6.0F + 1F,
                (16.0 / (level.random.nextFloat() * 0.8 + 0.2)).roundToInt() + 2,
                0f,
                grav,
                fric,
                false,
                false
            ),
            pos.x, pos.y + rand.nextDouble(0.15, 0.6), pos.z,
            xd, yd, zd
        )
    }
}

fun addFinalParticle(level: Level, x: Double, y: Double, z: Double, particleCount: Int = 1) {
    if (!level.isClientSide) return
    val rand = Random

    for (i in 0 ..< particleCount) {
        level.addParticle(
            ParticleTypes.POOF,
            x,
            y + rand.nextDouble(0.15, 0.6),
            z,
            rand.nextDouble(-0.06, 0.06),
            rand.nextDouble(-0.0, 0.15),
            rand.nextDouble(-0.06, 0.06)
        )
    }
}
